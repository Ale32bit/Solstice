package me.alexdevs.solstice.modules.smite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.smite.commands.SmiteCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class SmiteModule extends ModuleBase {
    

    public SmiteModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new SmiteCommand(this));
    }
}
