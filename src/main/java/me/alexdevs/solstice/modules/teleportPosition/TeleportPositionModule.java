package me.alexdevs.solstice.modules.teleportPosition;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.teleportPosition.commands.TeleportPositionCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class TeleportPositionModule extends ModuleBase {
    
    public TeleportPositionModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new TeleportPositionCommand(this));
    }
}
