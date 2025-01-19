package me.alexdevs.solstice.modules.afk.data;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerActivityState {
    public ServerLocation location;
    public int lastUpdate;
    public boolean isAfk;
    public boolean afkEnabled;

    public PlayerActivityState(ServerPlayerEntity player, int lastUpdate) {
        this.location = new ServerLocation(player);
        this.lastUpdate = lastUpdate;
        this.isAfk = false;
        this.afkEnabled = Permissions.check(player, Solstice.MOD_ID + "." + AfkModule.ID + ".base", true);
    }
}
