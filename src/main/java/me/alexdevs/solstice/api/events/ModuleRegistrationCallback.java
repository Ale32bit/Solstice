package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.module.ModuleBase;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.HashSet;

public interface ModuleRegistrationCallback {
    Event<ModuleRegistrationCallback> EVENT = EventFactory.createArrayBacked(ModuleRegistrationCallback.class,
            (listeners) -> () -> {
                var modules = new HashSet<ModuleBase>();
                for (ModuleRegistrationCallback listener : listeners) {
                    modules.addAll(listener.register());
                }

                return modules;
            });

    HashSet<? extends ModuleBase> register();
}
