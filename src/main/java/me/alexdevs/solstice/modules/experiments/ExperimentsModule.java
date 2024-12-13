package me.alexdevs.solstice.modules.experiments;

import me.alexdevs.solstice.modules.experiments.commands.TimeSpanCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ExperimentsModule {
    public static final boolean ENABLED = true;

    public static final String ID = "experiments";
    public ExperimentsModule() {
        if(!ENABLED) return;

        CommandRegistrationCallback.EVENT.register(TimeSpanCommand::new);
    }
}
