package me.alexdevs.solstice.api;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.back.BackModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class ServerLocation {
    protected final double x;
    protected final double y;
    protected final double z;
    protected final float yaw;
    protected final float pitch;
    protected final String world;

    public ServerLocation(double x, double y, double z, float yaw, float pitch, ServerLevel world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world.dimension().location().toString();
    }

    public ServerLocation(ServerPlayer player) {
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.yaw = player.getYRot();
        this.pitch = player.getXRot();
        this.world = player.serverLevel().dimension().location().toString();
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
            Solstice.modules.getModule(BackModule.class).setPlayerLastLocation(player.getUUID(), currentPosition);
        }

        var serverWorld = getWorld(player.getServer());

        player.setDeltaMovement(player.getDeltaMovement().multiply(1f, 0f, 1f));
        player.setOnGround(true);

        player.teleportTo(serverWorld, this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

        // There is a bug (presumably in Fabric's api) that causes experience level to be set to 0 when teleporting between dimensions/worlds.
        // Therefore, this will update the experience client side as a temporary solution.
        player.giveExperiencePoints(0);
    }

    public void teleport(ServerPlayer player) {
        teleport(player, true);
    }

    public ResourceKey<Level> getWorldKey() {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(this.getWorld()));
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
}
