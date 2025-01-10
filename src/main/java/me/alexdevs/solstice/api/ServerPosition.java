package me.alexdevs.solstice.api;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.back.BackModule;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class ServerPosition {
    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;
    protected String world;

    public ServerPosition(double x, double y, double z, float yaw, float pitch, ServerWorld world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world.getRegistryKey().getValue().toString();
    }

    public ServerPosition(ServerPlayerEntity player) {
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
        this.world = player.getServerWorld().getRegistryKey().getValue().toString();
    }

    public ServerPosition(double x, double y, double z, float yaw, float pitch, String worldKey) {
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
        ServerPosition that = (ServerPosition) o;
        return Double.compare(getX(), that.getX()) == 0 && Double.compare(getY(), that.getY()) == 0 && Double.compare(getZ(), that.getZ()) == 0 && Float.compare(getYaw(), that.getYaw()) == 0 && Float.compare(getPitch(), that.getPitch()) == 0 && Objects.equals(getWorld(), that.getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ(), getYaw(), getPitch(), getWorld());
    }

    public void teleport(ServerPlayerEntity player, boolean setBackPosition) {
        if (setBackPosition) {
            var currentPosition = new ServerPosition(player);
            Solstice.modules.getModule(BackModule.class).lastPlayerPositions.put(player.getUuid(), currentPosition);
        }

        var serverWorld = getWorld(player.getServer());

        player.setVelocity(player.getVelocity().multiply(1f, 0f, 1f));
        player.setOnGround(true);

        player.teleport(serverWorld, this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

        // There is a bug (presumably in Fabric's api) that causes experience level to be set to 0 when teleporting between dimensions/worlds.
        // Therefore, this will update the experience client side as a temporary solution.
        player.addExperience(0);
    }

    public void teleport(ServerPlayerEntity player) {
        teleport(player, true);
    }

    public RegistryKey<World> getWorldKey() {
        return RegistryKey.of(RegistryKeys.WORLD, new Identifier(this.getWorld()));
    }

    public ServerWorld getWorld(MinecraftServer server) {
        return server.getWorld(getWorldKey());
    }

    public BlockPos getBlockPos() {
        return BlockPos.ofFloored(this.getX(), this.getY(), this.getZ());
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
}
