package me.alexdevs.solstice.modules.heal;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.heal.commands.HealCommand;

public class HealModule extends ModuleBase.Toggleable {
    public static final String ID = "heal";

    public HealModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new HealCommand(this));
    }
}
