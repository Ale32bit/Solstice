package me.alexdevs.solstice.api;

import com.google.common.collect.ImmutableList;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

//? >= 1.21.4
//import java.util.Set;

import java.util.Objects;

public class ServerLocation {
    protected final double x;
    protected final double y;
    protected final double z;
    protected final float yaw;
    protected final float pitch;
    protected final String world;

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

    public static final int SAFE_RANGE = 5;

    public ServerLocation(double x, double y, double z, float yaw, float pitch, ServerLevel world) {
        this(x, y, z, yaw, pitch, world.dimension().location().toString());
    }

    public ServerLocation(ServerPlayer player) {
        this(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().location().toString());
    }

    public ServerLocation(double x, double y, double z, float yaw, float pitch, String worldKey) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = worldKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServerLocation that = (ServerLocation) o;
        return Double.compare(getX(), that.getX()) == 0 && Double.compare(getY(), that.getY()) == 0 && Double.compare(getZ(), that.getZ()) == 0 && Float.compare(getYaw(), that.getYaw()) == 0 && Float.compare(getPitch(), that.getPitch()) == 0 && Objects.equals(getWorld(), that.getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ(), getYaw(), getPitch(), getWorld());
    }

    public void teleport(ServerPlayer player, boolean setBackPosition) {
        if (setBackPosition) {
            var currentPosition = new ServerLocation(player);
            ModuleProvider.BACK.setPlayerLastLocation(player.getUUID(), currentPosition);
        }

        var serverWorld = getWorld(player.getServer());

        player.setDeltaMovement(player.getDeltaMovement().multiply(1f, 0f, 1f));
        player.setOnGround(true);

        //? >= 1.21.4
        //player.teleportTo(serverWorld, this.getX(), this.getY(), this.getZ(), Set.of(), this.getYaw(), this.getPitch(), false);
        //? < 1.21.4
        player.teleportTo(serverWorld, this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

        // There is a bug (presumably in Fabric's api) that causes experience level to be set to 0 when teleporting between dimensions/worlds.
        // Therefore, this will update the experience client side as a temporary solution.
        player.giveExperiencePoints(0);
    }

    public void teleport(ServerPlayer player) {
        teleport(player, true);
    }

    public ResourceKey<Level> getWorldKey() {
        return ResourceKey.create(Registries.DIMENSION, SolsticeIdentifier.parse(this.getWorld()).get());
    }

    public ServerLevel getWorld(MinecraftServer server) {
        return server.getLevel(getWorldKey());
    }

    public BlockPos getBlockPos() {
        return BlockPos.containing(this.getX(), this.getY(), this.getZ());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public String getWorld() {
        return world;
    }

    public double getDistance(ServerLocation other) {
        if (!other.getWorld().equals(this.getWorld())) {
            return Double.POSITIVE_INFINITY;
        }

        var thisVec = new Vec3(this.getX(), this.getY(), this.getZ());
        var otherVec = new Vec3(other.getX(), other.getY(), other.getZ());

        return thisVec.distanceTo(otherVec);
    }

    public Vec3 getDelta(ServerLocation other) {
        return new Vec3(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());
    }

    public boolean safeTeleport(ServerPlayer player, boolean setBackPosition, int range) {
        var world = getWorld(player.getServer());

        var horRange = (int) Math.pow(range, 2);
        for (int i = 1; i <= horRange; i++) {
            var rel = spiral(i);
            var attemptPos = this.getBlockPos().offset(rel);
            for (int j = 0; j < range * 2; j++) {

                // Integer zigzag pattern: 0, 1, -1, 2, -2, 3, -3, ...
                var y = (j % 2 == 0) ? -j / 2 : j / 2 + 1;

                //? if >= 26.1 {
                /*var safePos = DismountHelper.findSafeDismountLocation(net.minecraft.world.entity.EntityTypes.PLAYER, world, attemptPos.offset(0, y, 0), true);
                *///? } elif >= 1.21.1 {
                var safePos = DismountHelper.findSafeDismountLocation(EntityType.PLAYER, world, attemptPos.offset(0, y, 0), true);
                //? }
                if (safePos != null) {
                    var safeLocation = new ServerLocation(safePos.x, safePos.y, safePos.z, this.getYaw(), this.getPitch(), this.getWorld());
                    safeLocation.teleport(player, setBackPosition);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean safeTeleport(ServerPlayer player) {
        return safeTeleport(player, true, SAFE_RANGE);
    }

    /// [Algorithm to find a specific element coordinates in a spiral](https://stackoverflow.com/questions/61229890/algorithm-to-find-a-specific-elements-coordinate-in-a-spiral)
    public static Vec3i spiral(int n) {
        if (n == 0) {
            return Vec3i.ZERO;
        }

        var k = (int) Math.ceil((Math.sqrt(n) - 1) / 2);
        var t = 2 * k + 1;
        var m = (int) Math.pow(t, 2);
        t = t - 1;

        if (n >= m - t) {
            var x = k - (m - n);
            var z = -k;
            return new Vec3i(x, 0, z);
        } else {
            m = m - t;
        }

        if (n >= m - t) {
            var x = -k;
            var z = -k + (m - n);
            return new Vec3i(x, 0, z);
        } else {
            m = m - t;
        }

        if (n >= m - t) {
            var x = -k + (m - n);
            var z = k;
            return new Vec3i(x, 0, z);
        } else {
            var x = k;
            var z = k - (m - n - t);
            return new Vec3i(x, 0, z);
        }
    }
}
