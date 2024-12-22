package me.alexdevs.solstice.modules.teleportHere;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportHere.commands.TeleportHereCommand;

public class TeleportHereModule extends ModuleBase {
    public static final String ID = "teleporthere";

    public TeleportHereModule() {
        super(ID);

        commands.add(new TeleportHereCommand(this));
    }
}
