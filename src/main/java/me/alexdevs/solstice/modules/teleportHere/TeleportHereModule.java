package me.alexdevs.solstice.modules.teleportHere;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportHere.commands.TeleportHereCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class TeleportHereModule extends ModuleBase.Toggleable {
    

    public TeleportHereModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new TeleportHereCommand(this));
    }
}
