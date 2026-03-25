package me.alexdevs.solstice.modules.ignite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ignite.commands.IgniteCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class IgniteModule extends ModuleBase.Toggleable {
    

    public IgniteModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new IgniteCommand(this));
    }
}
