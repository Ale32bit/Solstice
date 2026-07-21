package me.alexdevs.solstice.modules.kick;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.kick.commands.KickCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class KickModule extends ModuleBase {

    public KickModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new KickCommand(this));
    }
}
