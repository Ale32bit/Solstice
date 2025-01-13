package me.alexdevs.solstice.modules.rtp.core;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.modules.rtp.data.RTPConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Locator {

    public static final ChunkTicketType<BlockPos> RTP_TICKET = ChunkTicketType.create("rtp", Comparator.comparingLong(ChunkPos::toLong), 300);

    public final ServerPlayerEntity player;
    public final ServerWorld world;
    public final RTPConfig config;

    private Consumer<Result> callback;
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();

    private Chunk chunk;
    private BlockPos attemptPos;
    private boolean failed = false;

//    private static final ImmutableList<Vec3i> VALID_HORIZONTAL_SPAWN_OFFSETS = ImmutableList.of(
//            new Vec3i(0, 0, 0),
//            new Vec3i(0, 0, -1),
//            new Vec3i(-1, 0, 0),
//            new Vec3i(0, 0, 1),
//            new Vec3i(1, 0, 0),
//            new Vec3i(-1, 0, -1),
//            new Vec3i(1, 0, -1),
//            new Vec3i(-1, 0, 1),
//            new Vec3i(1, 0, 1)
//    );

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

    public Locator(ServerPlayerEntity player, ServerWorld world, RTPConfig config) {
        this.player = player;
        this.world = world;
        this.config = config;
    }

    public void locate(Consumer<Result> callback) {
        this.callback = callback;
        stopwatch.start();
        attempt(config.attempts);
    }

    private void attempt(int remainingAttempts) {
        if (remainingAttempts == 0) {
            failed = true;
            callback.accept(new Result(Result.Type.TOO_MANY_ATTEMPTS, Optional.empty()));
            return;
        }

        var pos = getRandomPos();

        if (isValid(pos)) {
            attemptPos = pos;
            load();
        } else {
            attempt(remainingAttempts - 1);
        }
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
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
    }

    private BlockPos getEmptySpace(BlockPos pos) {
        var bottom = chunk.getBottomY();
        var top = world.getLogicalHeight();
        var blockPos = new BlockPos.Mutable(pos.getX(), top, pos.getZ());

        var isAir = false;
        var isAirBelow = false;
        while (blockPos.getY() >= bottom && isAirBelow || !isAir) {
            isAir = isAirBelow;
            isAirBelow = chunk.getBlockState(blockPos.move(Direction.DOWN)).isAir();
        }
        return blockPos.up().toImmutable();
    }

    private void findValidPlacement() {
        BlockPos pos = attemptPos;
        for (var i = 0; i <= 256; i++) {
            if (world.getDimension().hasCeiling()) {
                pos = getEmptySpace(pos);
            } else {
                pos = getTopBlock(pos);
            }
            var bs = chunk.getBlockState(pos);
            var bsBelow = chunk.getBlockState(pos.down());
            if (!unsafeBlocks.contains(bs.getBlock()) && !unsafeBlocks.contains(bsBelow.getBlock())) {
                break;
            }

            var dx = i % 16;
            var dz = i / 16;
            pos = chunk.getPos().getBlockPos(dx, chunk.getBottomY(), dz);
        }

        if (pos.getY() <= chunk.getBottomY()) {
            callback.accept(new Result(Result.Type.UNSAFE, Optional.empty()));
            return;
        }

        var vec = pos.toCenterPos();

        callback.accept(new Result(Result.Type.SUCCESS, Optional.of(new ServerLocation(
                vec.getX(), vec.getY(), vec.getZ(), 0, 0, world
        ))));
    }

    private void load() {
        world.getChunkManager().addTicket(RTP_TICKET, new ChunkPos(attemptPos), 0, attemptPos);
    }

    private Optional<WorldChunk> getChunk(ChunkPos pos) {
        var holder = world.getChunkManager().getChunkHolder(pos.toLong());
        if (holder == null) {
            return Optional.empty();
        } else {
            var chunk = holder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).orElse(null);
            if (chunk == null) {
                return Optional.empty();
            }
            return Optional.of(chunk);
        }
    }

    public boolean isValid(BlockPos pos) {
        if(pos == null)
            return false;

        var biome = world.getBiome(pos);
        return !config.parseBiomes().contains(biome.getKey().orElse(null));
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
        for(var i = 0; i <= limit; i++) {
            var dist = world.getRandom().nextDouble() * (max - min) + min;
            var angle = world.getRandom().nextDouble() * Math.PI * 2d;
            x = (int) (Math.cos(angle) * dist + centerX);
            z = (int) (Math.sin(angle) * dist + centerZ);

            if(worldBorder.contains(x, z))
                break;

            if(i == limit) {
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
