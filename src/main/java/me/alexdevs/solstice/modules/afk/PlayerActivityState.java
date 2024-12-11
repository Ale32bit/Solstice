package me.alexdevs.solstice.modules.afk;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerActivityState {
    public PlayerPosition position;
    public int lastUpdate;
    public boolean isAfk;
    public int activeStart;

    public PlayerActivityState(ServerPlayerEntity player, int lastUpdate) {
        this.position = new PlayerPosition(player);
        this.lastUpdate = lastUpdate;
        this.isAfk = false;
        this.activeStart = lastUpdate;
    }
}
