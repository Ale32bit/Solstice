package me.alexdevs.solstice.modules.core;

import me.alexdevs.solstice.ServiceProvider;
import me.alexdevs.solstice.api.module.CommandProvider;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.core.commands.SolsticeCommand;

import java.util.Collection;
import java.util.List;

public class CoreModule extends ModuleBase {
    public CoreModule(ServiceProvider service) {
        super(service);
    }

    @Override
    public Collection<Class<? extends CommandProvider>> getCommands() {
        return List.of(
                SolsticeCommand.class
        );
    }
}
