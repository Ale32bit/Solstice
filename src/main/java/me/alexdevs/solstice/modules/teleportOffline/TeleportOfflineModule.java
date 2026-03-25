package me.alexdevs.solstice.modules.teleportOffline;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportOffline.commands.TeleportOfflineCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class TeleportOfflineModule extends ModuleBase.Toggleable {
    

    public TeleportOfflineModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new TeleportOfflineCommand(this));
    }
}
