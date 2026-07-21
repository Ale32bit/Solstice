package me.alexdevs.solstice.modules.teleportHere;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.teleportHere.commands.TeleportHereCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class TeleportHereModule extends ModuleBase {
    

    public TeleportHereModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new TeleportHereCommand(this));
    }
}
