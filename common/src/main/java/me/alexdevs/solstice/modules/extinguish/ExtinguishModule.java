package me.alexdevs.solstice.modules.extinguish;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.extinguish.commands.ExtinguishCommand;

public class ExtinguishModule extends ModuleBase.Toggleable {
    public static final String ID = "extinguish";

    public ExtinguishModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new ExtinguishCommand(this));
    }
}
