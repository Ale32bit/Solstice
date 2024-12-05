package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.ServiceProvider;

import java.util.Collection;
import java.util.List;

public abstract class ModuleBase {
    protected final ServiceProvider provider;

    public ModuleBase(ServiceProvider service) {
        provider = service;
    }

    public Collection<Class<? extends CommandProvider>> getCommands() {
        return List.of();
    }


}
