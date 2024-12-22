package me.alexdevs.solstice.modules.suicide;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.suicide.commands.SuicideCommand;

public class SuicideModule extends ModuleBase {
    public static final String ID = "suicide";

    public SuicideModule() {
        super(ID);
        commands.add(new SuicideCommand(this));
    }
}
