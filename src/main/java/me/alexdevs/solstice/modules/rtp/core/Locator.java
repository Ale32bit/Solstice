package me.alexdevs.solstice.modules.rtp.core;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.modules.rtp.data.RTPConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
            Blocks.POWDER_SNOW,
            Blocks.WATER
    );

    public Locator(ServerWorld world, RTPConfig config) {
        this.world = world;
        this.config = config;
    }

    public void locate(Consumer<Result> callback) {
        this.callback = callback;
        stopwatch.start();
        Solstice.scheduler.schedule(() -> this.attempt(config.attempts), 0, TimeUnit.MILLISECONDS);
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
            pos = new BlockPos(dx, 0, dz);
        }

        if (pos.getY() <= chunk.getBottomY()) {
            callback.accept(new Result(Result.Type.UNSAFE, Optional.empty()));
            return;
        }

        var vec = pos.toCenterPos();

        callback.accept(new Result(Result.Type.SUCCESS, Optional.of(new ServerPosition(
                vec.getX(), vec.getY(), vec.getZ(), 0, 0, world
        ))));
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

    private void load() {
        world.getChunkManager().addTicket(RTP_TICKET, new ChunkPos(attemptPos), 0, attemptPos);
    }

    private Optional<WorldChunk> getChunk(ChunkPos pos) {
        var holder = world.getChunkManager().getChunkHolder(pos.toLong());
        if (holder == null) {
            return Optional.empty();
        } else {
            return holder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
        }
    }

    public boolean isValid(BlockPos pos) {
        var biome = world.getBiome(pos);
        return !config.parseBiomes().contains(biome.getKey().orElse(null));
    }

    public BlockPos getRandomPos() {
        var worldBorder = world.getWorldBorder();
        var size = worldBorder.getSize();
        var centerX = worldBorder.getCenterX();
        var centerZ = worldBorder.getCenterZ();

        var max = Math.min((int) size, config.maxRadius);
        var min = Math.max(0, config.minRadius);

        var x = (int) (world.getRandom().nextBetween(min, max) + centerX);
        var z = (int) (world.getRandom().nextBetween(min, max) + centerZ);

        return new BlockPos(x, world.getLogicalHeight(), z);
    }

    public record Result(Type type, Optional<ServerPosition> position) {
        public enum Type {
            SUCCESS,
            TOO_MANY_ATTEMPTS,
            TIMEOUT,
            UNSAFE
        }
    }
}
