package me.alexdevs.solstice.modules.experiments;

import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.experiments.commands.TimeSpanCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.Collection;
import java.util.List;

public class ExperimentsModule extends ModuleBase {
    public static final boolean ENABLED = false;

    private final List<ModCommand<ExperimentsModule>> commands = List.of(
            new TimeSpanCommand(this)
    );

    public static final String ID = "experiments";
    public ExperimentsModule() {
        super(ID);

    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        if(!ENABLED) return List.of();

        return commands;
    }
}
