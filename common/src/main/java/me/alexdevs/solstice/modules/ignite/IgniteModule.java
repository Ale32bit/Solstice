package me.alexdevs.solstice.modules.ignite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ignite.commands.IgniteCommand;

public class IgniteModule extends ModuleBase.Toggleable {
    public static final String ID = "ignite";

    public IgniteModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new IgniteCommand(this));
    }
}
