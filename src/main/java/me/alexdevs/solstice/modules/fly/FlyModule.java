package me.alexdevs.solstice.modules.fly;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.fly.commands.FlyCommand;

public class FlyModule extends ModuleBase {
    public static final String ID = "fly";

    public FlyModule() {
        super(ID);

        commands.add(new FlyCommand(this));
    }
}
