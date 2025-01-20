package me.alexdevs.solstice.modules.teleportHere;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportHere.commands.TeleportHereCommand;

public class TeleportHereModule extends ModuleBase.Toggleable {
    public static final String ID = "teleporthere";

    public TeleportHereModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new TeleportHereCommand(this));
    }
}
