package me.alexdevs.solstice.modules.kick;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.kick.commands.KickCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class KickModule extends ModuleBase.Toggleable {

    public KickModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new KickCommand(this));
    }
}
