package me.alexdevs.solstice.modules.afk;

import me.alexdevs.solstice.api.ServerLocation;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerActivityStateOld {
    public ServerLocation position;
    public int lastUpdate;
    public boolean isAfk;
    public int activeStart;

    public PlayerActivityStateOld(ServerPlayerEntity player, int lastUpdate) {
        this.position = new ServerLocation(player);
        this.lastUpdate = lastUpdate;
        this.isAfk = false;
        this.activeStart = lastUpdate;
    }
}
