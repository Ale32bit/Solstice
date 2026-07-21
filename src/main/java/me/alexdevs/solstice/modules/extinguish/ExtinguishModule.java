package me.alexdevs.solstice.modules.extinguish;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.extinguish.commands.ExtinguishCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class ExtinguishModule extends ModuleBase {
    

    public ExtinguishModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new ExtinguishCommand(this));
    }
}
