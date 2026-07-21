package me.alexdevs.solstice.modules.teleportOffline;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.teleportOffline.commands.TeleportOfflineCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class TeleportOfflineModule extends ModuleBase {
    

    public TeleportOfflineModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new TeleportOfflineCommand(this));
    }
}
