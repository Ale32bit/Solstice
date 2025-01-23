package me.alexdevs.solstice.modules.teleportOffline;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportOffline.commands.TeleportOfflineCommand;

public class TeleportOfflineModule extends ModuleBase.Toggleable {
    public static final String ID = "teleportoffline";

    public TeleportOfflineModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new TeleportOfflineCommand(this));
    }
}
