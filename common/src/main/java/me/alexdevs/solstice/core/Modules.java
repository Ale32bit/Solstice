package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.proxy.ProxyCommandRegistrationCallback;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleEntrypoint;
import me.alexdevs.solstice.core.coreModule.CoreModule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;

public class Modules {

    private final HashSet<ModuleBase> modules = new HashSet<>();

    public Modules() {
        ProxyCommandRegistrationCallback.EVENT.register((dispatcher, buildCtx, selection) -> {
            for (var module : modules) {
                for (var command : module.getCommands()) {
                    command.register(dispatcher, buildCtx, selection);
                }
            }
        });
    }

    public void register() {
        modules.add(new CoreModule());

        for (var provider : ServiceLoader.load(ModuleEntrypoint.class)) {
            registerModule(provider);
        }
    }

    private void registerModule(ModuleEntrypoint provider) {
        Solstice.LOGGER.info("Registering module provider '{}'", provider.getClass().getName());

        try {
            var providerModules = provider.register();

            for (var entry : providerModules) {
                var moduleId = entry.getId();

                if (modules.stream().anyMatch(m -> m.getId().equals(moduleId))) {
                    Solstice.LOGGER.warn("Module ID conflict: {}", entry.getId());
                    continue;
                }

                modules.add(entry);
            }
        } catch (Exception e) {
            Solstice.LOGGER.error("Error registering a module from {}", provider.getClass().getName(), e);
        }
    }

    public Collection<? extends ModuleBase> getModules() {
        return Collections.unmodifiableSet(modules);
    }

    public <T> T getModule(Class<T> classOfModule) {
        for (var module : modules) {
            if (classOfModule.isInstance(module)) {
                return classOfModule.cast(module);
            }
        }
        return null;
    }

    public Collection<? extends ModuleBase> getEnabledModules() {
        var set = new HashSet<ModuleBase>();
        getModules().forEach(module -> {
            if (module instanceof ModuleBase.Toggleable toggleable) {
                if (toggleable.isEnabled()) {
                    set.add(module);
                }
            } else {
                set.add(module);
            }
        });
        return Collections.unmodifiableSet(set);
    }

    public void initModules() {
        var enabledModules = getEnabledModules();
        for (var module : enabledModules) {
            try {
                module.init();
            } catch (NoSuchMethodError e) {
                Solstice.LOGGER.error("Legacy module {} does not contain the init method. UPDATE!", module.getId(), e);
            } catch (Exception e) {
                Solstice.LOGGER.error("Error initializing module {}", module.getId(), e);
            }
        }
    }
}
