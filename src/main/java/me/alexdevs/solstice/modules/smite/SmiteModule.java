package me.alexdevs.solstice.modules.smite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.smite.commands.SmiteCommand;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class SmiteModule extends ModuleBase.Toggleable {
    

    public SmiteModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        commands.add(new SmiteCommand(this));
    }
}
