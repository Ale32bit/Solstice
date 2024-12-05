package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.core.BossBarManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ModuleEvents {
    public static final Event<Registration> REGISTRATION = EventFactory.createArrayBacked(Registration.class, callbacks ->
            () -> {
                var modules = new HashSet<Class<? extends ModuleBase>>();
                for (Registration callback : callbacks) {
                    modules.addAll(callback.register());
                }
                return Collections.unmodifiableCollection(modules);
            });

    public static final Event<Setup> SETUP = EventFactory.createArrayBacked(Setup.class, callbacks ->
            () -> {
                for (Setup callback : callbacks) {
                    callback.setup();
                }
            });

    @FunctionalInterface
    public interface Registration {
        Collection<Class<? extends ModuleBase>> register();
    }

    @FunctionalInterface
    public interface Setup {
        void setup();
    }
}
