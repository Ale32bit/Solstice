package me.alexdevs.solstice.api;

import com.google.gson.annotations.Expose;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.afk.PlayerPosition;
import me.alexdevs.solstice.modules.back.BackModule;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

@ConfigSerializable
public class ServerPosition {
    @Expose
    public double x;
    @Expose
    public double y;
    @Expose
    public double z;
    @Expose
    public float yaw;
    @Expose
    public float pitch;
    @Expose
    public String world;

    public ServerPosition() {

    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServerPosition that = (ServerPosition) o;
        return Double.compare(x, that.x) == 0 && Double.compare(y, that.y) == 0 && Double.compare(z, that.z) == 0 && Float.compare(yaw, that.yaw) == 0 && Float.compare(pitch, that.pitch) == 0 && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, yaw, pitch, world);
    }

    public void teleport(ServerPlayerEntity player, boolean setBackPosition) {
        if (setBackPosition) {
            var currentPosition = new ServerPosition(player);
            Solstice.modules.getModule(BackModule.class).lastPlayerPositions.put(player.getUuid(), currentPosition);
        }

        var serverWorld = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(this.world)));

        player.setVelocity(player.getVelocity().multiply(1f, 0f, 1f));
        player.setOnGround(true);

        player.teleport(serverWorld, this.x, this.y, this.z, this.yaw, this.pitch);

        // There is a bug (presumably in Fabric's api) that causes experience level to be set to 0 when teleporting between dimensions/worlds.
        // Therefore, this will update the experience client side as a temporary solution.
        player.addExperience(0);
    }

    public void teleport(ServerPlayerEntity player) {
        teleport(player, true);
    }
}
