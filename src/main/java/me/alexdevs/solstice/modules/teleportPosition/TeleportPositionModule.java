package me.alexdevs.solstice.modules.teleportPosition;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportPosition.commands.TeleportPositionCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class TeleportPositionModule extends ModuleBase.Toggleable {
    
    public TeleportPositionModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new TeleportPositionCommand(this));
    }
}
