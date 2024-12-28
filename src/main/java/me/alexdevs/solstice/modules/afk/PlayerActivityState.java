package me.alexdevs.solstice.modules.afk;

import me.alexdevs.solstice.api.ServerPosition;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerActivityState {
    public ServerPosition position;
    public int lastUpdate;
    public boolean isAfk;
    public int activeStart;

    public PlayerActivityState(ServerPlayerEntity player, int lastUpdate) {
        this.position = new ServerPosition(player);
        this.lastUpdate = lastUpdate;
        this.isAfk = false;
        this.activeStart = lastUpdate;
    }
}
