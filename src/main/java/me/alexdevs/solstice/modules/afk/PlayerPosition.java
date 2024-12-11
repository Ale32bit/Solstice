package me.alexdevs.solstice.modules.afk;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerPosition {
    public String dimension;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;


    public boolean equals(PlayerPosition obj) {
        return x == obj.x && y == obj.y && z == obj.z
                && yaw == obj.yaw && pitch == obj.pitch
                && dimension.equals(obj.dimension);
    }

    public PlayerPosition(ServerPlayerEntity player) {
        dimension = player.getWorld().getRegistryKey().getValue().toString();
        x = player.getX();
        y = player.getY();
        z = player.getZ();
        yaw = player.getYaw();
        pitch = player.getPitch();
    }
}
