package me.alexdevs.solstice.modules.smite;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.smite.commands.SmiteCommand;

public class SmiteModule extends ModuleBase.Toggleable {
    public static final String ID = "smite";

    public SmiteModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new SmiteCommand(this));
    }
}
