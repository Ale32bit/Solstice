package me.alexdevs.solstice.modules.ignite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.ignite.commands.IgniteCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class IgniteModule extends ModuleBase {
    

    public IgniteModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        commands.add(new IgniteCommand(this));
    }
}
