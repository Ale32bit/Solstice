package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.core.ServiceProvider;

import java.util.Collection;

public interface IModule {
    default void initialize(ServiceProvider provider) {}
    default void postInitialize(ServiceProvider provider) {}

    Collection<Class<? extends ModuleCommand>> getCommands();
}
