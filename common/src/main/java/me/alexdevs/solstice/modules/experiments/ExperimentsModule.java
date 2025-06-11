package me.alexdevs.solstice.modules.experiments;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.experiments.commands.FlagsCommand;
import me.alexdevs.solstice.modules.experiments.commands.TimeSpanCommand;

import java.util.Collection;
import java.util.List;

public class ExperimentsModule extends ModuleBase {
    public static final boolean ENABLED = false;
    public static final String ID = "experiments";

    public ExperimentsModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new TimeSpanCommand(this));
        commands.add(new FlagsCommand(this));
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        if (!ENABLED) return List.of();

        return commands;
    }
}
