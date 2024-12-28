package me.alexdevs.solstice.modules.god;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.god.commands.GodCommand;

public class GodModule extends ModuleBase {
    public static final String ID = "god";

    public GodModule() {
        super(ID);

        commands.add(new GodCommand(this));
    }
}
