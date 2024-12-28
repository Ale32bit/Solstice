package me.alexdevs.solstice.modules.heal;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.heal.commands.HealCommand;

public class HealModule extends ModuleBase {
    public static final String ID = "heal";

    public HealModule() {
        super(ID);
        commands.add(new HealCommand(this));
    }
}
