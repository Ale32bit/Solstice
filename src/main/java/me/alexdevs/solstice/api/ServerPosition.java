package me.alexdevs.solstice.api;

import me.alexdevs.solstice.core.BackTracker;
import com.google.gson.annotations.Expose;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

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

    public void teleport(ServerPlayerEntity player, boolean setBackPosition) {
        if (setBackPosition) {
            var currentPosition = new ServerPosition(player);
            BackTracker.lastPlayerPositions.put(player.getUuid(), currentPosition);
        }

        var serverWorld = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(this.world)));

        player.setVelocity(player.getVelocity().multiply(1f, 0f, 1f));
        player.setOnGround(true);

        player.teleport(
                serverWorld,
                this.x,
                this.y,
                this.z,
                this.yaw,
                this.pitch
        );
    }

    public void teleport(ServerPlayerEntity player) {
        teleport(player, true);
    }
}
