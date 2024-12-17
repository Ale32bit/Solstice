package me.alexdevs.solstice.modules.admin;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.admin.commands.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class AdminModule extends ModuleBase {
    public static final String ID = "admin";
    public AdminModule() {
        super(ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new BroadcastCommand(this);
            new DoAsCommand(dispatcher, registryAccess, environment);
            new ExtinguishCommand(dispatcher, registryAccess, environment);
            new FeedCommand(dispatcher, registryAccess, environment);
            new FlyCommand(dispatcher, registryAccess, environment);
            new GodCommand(dispatcher, registryAccess, environment);
            new HealCommand(dispatcher, registryAccess, environment);
            new IgniteCommand(dispatcher, registryAccess, environment);
            new InventorySeeCommand(dispatcher, registryAccess, environment);
            new SmiteCommand(dispatcher, registryAccess, environment);
            new SudoCommand(dispatcher, registryAccess, environment);
        });
    }
}
