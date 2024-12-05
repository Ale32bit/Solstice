package me.alexdevs.solstice.modules;

import me.alexdevs.solstice.api.events.ModuleEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.core.CoreModule;

import java.util.Collection;
import java.util.List;

public class ModuleProvider {
    public static void register() {
        ModuleEvents.REGISTRATION.register(ModuleProvider::getModules);
    }

    private static Collection<Class<? extends ModuleBase>> getModules() {
        return List.of(
                CoreModule.class
        );
    }
}
