package me.alexdevs.solstice.modules.afk.data;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;

public class PlayerActivityState {
    public ServerLocation location;
    public int lastUpdate;
    public boolean isAfk;
    public boolean afkEnabled;
    public boolean activeTimeEnabled;

    public PlayerActivityState(ServerPlayer player, int lastUpdate) {
        var module = ModuleProvider.AFK;
        this.location = new ServerLocation(player);
        this.lastUpdate = lastUpdate;
        this.isAfk = false;
        this.afkEnabled = Permissions.check(player, module.getPermissionNode(), true);
        this.activeTimeEnabled = Permissions.check(player, module.getPermissionNode("activetime"), true);
    }
}
