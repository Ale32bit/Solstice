package me.alexdevs.solstice.api;

import me.alexdevs.solstice.api.module.ModuleContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IServiceProvider {
    Collection<ModuleContainer> getModules();
    @Nullable ModuleContainer getModule(String id);
}
