package me.alexdevs.solstice.modules.suicide;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.suicide.commands.SuicideCommand;

public class SuicideModule extends ModuleBase.Toggleable {
    public static final String ID = "suicide";

    public SuicideModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new SuicideCommand(this));
    }
}
