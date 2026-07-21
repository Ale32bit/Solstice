package me.alexdevs.solstice.modules.suicide;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.suicide.commands.SuicideCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class SuicideModule extends ModuleBase {
    

    public SuicideModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new SuicideCommand(this));
    }
}
