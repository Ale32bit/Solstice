package me.alexdevs.solstice.modules.teleportPosition;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportPosition.commands.TeleportPositionCommand;

public class TeleportPositionModule extends ModuleBase.Toggleable {
    public static final String ID = "teleportposition";
    public TeleportPositionModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new TeleportPositionCommand(this));
    }
}
