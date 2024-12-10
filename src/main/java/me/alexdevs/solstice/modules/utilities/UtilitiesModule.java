package me.alexdevs.solstice.modules.utilities;

import me.alexdevs.solstice.modules.utilities.commands.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class UtilitiesModule {
    public UtilitiesModule() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new AnvilCommand(dispatcher, registryAccess, environment);
            new CartographyCommand(dispatcher, registryAccess, environment);
            new EnderchestCommand(dispatcher, registryAccess, environment);
            new GrindstoneCommand(dispatcher, registryAccess, environment);
            new LoomCommand(dispatcher, registryAccess, environment);
            new SmithingCommand(dispatcher, registryAccess, environment);
            new StonecutterCommand(dispatcher, registryAccess, environment);
            new SuicideCommand(dispatcher, registryAccess, environment);
            new TrashCommand(dispatcher, registryAccess, environment);
            new WorkbenchCommand(dispatcher, registryAccess, environment);
        });
    }
}
