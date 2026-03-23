package me.alexdevs.solstice.modules.rtp.core;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.modules.rtp.data.RTPConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Locator {

    public static final TicketType<BlockPos> RTP_TICKET = TicketType.create("rtp", Comparator.comparingLong(ChunkPos::asLong), 300);

    public final ServerPlayer player;
    public final ServerLevel world;
    public final RTPConfig config;
    public @Nullable
    final ResourceKey<Biome> biome;

    private Consumer<Result> callback;
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();

    private ChunkAccess chunk;
    private BlockPos attemptPos;
    private boolean failed = false;
    private int remainingAttempts = 0;

    private static final ImmutableList<Block> unsafeBlocks = ImmutableList.of(
            Blocks.LAVA,
            Blocks.MAGMA_BLOCK,
            Blocks.CACTUS,
            Blocks.FIRE,
            Blocks.CAMPFIRE,
            Blocks.LAVA_CAULDRON,
            Blocks.SWEET_BERRY_BUSH,
            Blocks.POWDER_SNOW
    );

    private static final ImmutableList<Block> nonIdealBlocks = ImmutableList.of(
            Blocks.WATER
    );

    public Locator(ServerPlayer player, ServerLevel world, RTPConfig config) {
        this(player, world, config, null);
    }

    public Locator(ServerPlayer player, ServerLevel world, RTPConfig config, @Nullable ResourceKey<Biome> biome) {
        this.player = player;
        this.world = world;
        this.config = config;
        this.biome = biome;
        this.remainingAttempts = config.attempts;
    }

    public void locate(Consumer<Result> callback) {
        this.callback = callback;
        stopwatch.start();
        attempt();
    }

    private void attempt() {
        for (var i = config.attempts; i >= 0; i--) {
            this.remainingAttempts = i;
            var pos = getRandomPos();

            if (isValid(pos)) {
                Solstice.LOGGER.info("RTP spot found at attempt {} for {}", config.attempts - remainingAttempts, player.getName());
                attemptPos = pos;
                load();
                return;
            }
        }

        failed = true;
        callback.accept(new Result(Result.Type.TOO_MANY_ATTEMPTS, Optional.empty()));
    }

    public boolean tick() {
        if (failed) return true;

        if (stopwatch.elapsed(TimeUnit.MILLISECONDS) >= config.timeout) {
            callback.accept(new Result(Result.Type.TIMEOUT, Optional.empty()));
            return true;
        }

        // Not yet started
        if (attemptPos == null) {
            return false;
        }

        var chunk = getChunk(new ChunkPos(attemptPos));
        if (chunk.isPresent()) {
            this.chunk = chunk.get();
            findValidPlacement();
            return true;
        }

        return false;
    }

    private BlockPos getTopBlock(BlockPos pos) {
        return world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
    }

    private BlockPos getEmptySpace(BlockPos pos) {
        var bottom = chunk.getMinBuildHeight();
        var top = world.getLogicalHeight();
        var blockPos = new BlockPos.MutableBlockPos(pos.getX(), top, pos.getZ());

        var isAir = false;
        var isAirBelow = false;
        while (blockPos.getY() >= bottom && isAirBelow || !isAir) {
            isAir = isAirBelow;
            isAirBelow = chunk.getBlockState(blockPos.move(Direction.DOWN)).isAir();
        }
        return blockPos.above().immutable();
    }

    private void findValidPlacement() {
        BlockPos pos = attemptPos;
        for (var i = 0; i <= 256; i++) {
            if (world.dimensionType().hasCeiling()) {
                pos = getEmptySpace(pos);
            } else {
                pos = getTopBlock(pos);
            }
            var bs = chunk.getBlockState(pos);
            var bsBelow = chunk.getBlockState(pos.below());
            if (!unsafeBlocks.contains(bs.getBlock()) && !unsafeBlocks.contains(bsBelow.getBlock())) {
                break;
            }

            var dx = i % 16;
            var dz = i / 16;
            pos = chunk.getPos().getBlockAt(dx, chunk.getMinBuildHeight(), dz);
        }

        if (pos.getY() <= chunk.getMinBuildHeight()) {
            callback.accept(new Result(Result.Type.UNSAFE, Optional.empty()));
            return;
        }

        var vec = pos.getCenter();

        callback.accept(new Result(Result.Type.SUCCESS, Optional.of(new ServerLocation(
                vec.x(), vec.y(), vec.z(), 0, 0, world
        ))));
    }

    private void load() {
        world.getChunkSource().addRegionTicket(RTP_TICKET, new ChunkPos(attemptPos), 0, attemptPos);
    }

    private Optional<LevelChunk> getChunk(ChunkPos pos) {
        var holder = world.getChunkSource().getVisibleChunkIfPresent(pos.toLong());
        if (holder == null) {
            return Optional.empty();
        } else {
            //? if >= 1.21.1 {
            /*var chunk = holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).orElse(null);*/
            //? } else {
            var chunk = holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left().orElse(null);
            //? }
            if (chunk == null) {
                return Optional.empty();
            }
            return Optional.of(chunk);
        }
    }

    public boolean isValid(BlockPos pos) {
        if (pos == null)
            return false;

        if (this.biome != null) {
            return isInBiome(pos, this.biome);
        }

        var biome = world.getBiome(pos);
        return !config.parseBiomes().contains(biome.unwrapKey().orElse(null));
    }

    public boolean isInBiome(BlockPos pos, ResourceKey<Biome> biome) {
        var biomeAtPos = world.getBiome(pos);
        return biomeAtPos.unwrapKey().get().equals(biome);
    }

    public BlockPos getRandomPos() {
        var worldBorder = world.getWorldBorder();
        var size = worldBorder.getSize();

        double centerX, centerZ;
        if (config.aroundPlayer) {
            centerX = player.getX();
            centerZ = player.getZ();
        } else {
            centerX = worldBorder.getCenterX();
            centerZ = worldBorder.getCenterZ();
        }

        var maxDiameter = config.maxRadius * 2;
        var minDiameter = config.minRadius * 2;

        var max = Math.min((int) size, maxDiameter);
        var min = Math.max(0, minDiameter);

        int x = 0;
        int z = 0;
        var limit = 256;
        for (var i = 0; i <= limit; i++) {
            var dist = world.getRandom().nextDouble() * (max - min) + min;
            var angle = world.getRandom().nextDouble() * Math.PI * 2d;
            x = (int) (Math.cos(angle) * dist + centerX);
            z = (int) (Math.sin(angle) * dist + centerZ);

            if (worldBorder.isWithinBounds(x, z))
                break;

            if (i == limit) {
                return null;
            }
        }

        return new BlockPos(x, world.getLogicalHeight(), z);
    }

    public record Result(Type type, Optional<ServerLocation> position) {
        public enum Type {
            SUCCESS,
            TOO_MANY_ATTEMPTS,
            TIMEOUT,
            UNSAFE
        }
    }
}
