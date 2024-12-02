package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.module.ModuleContainer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Collection;
import java.util.HashSet;

public interface ModuleRegistrationCallback {
    Event<ModuleRegistrationCallback> EVENT = EventFactory.createArrayBacked(ModuleRegistrationCallback.class, callbacks ->
            () -> {
                var modules = new HashSet<ModuleContainer>();
                for (ModuleRegistrationCallback callback : callbacks) {
                    modules.addAll(callback.register());
                }
                return (Collection<ModuleContainer>) modules;
            });

    Collection<ModuleContainer> register();
}
