package me.alexdevs.solstice.core;

import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleEntrypoint;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Modules {

    private final HashSet<ModuleBase> modules = new HashSet<>();

    public Modules() {
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    public void register() {
        modules.add(new CoreModule());

        var fabric = FabricLoader.getInstance();
        var moduleContainers = fabric.getEntrypointContainers("solstice", ModuleEntrypoint.class);
        for (var container : moduleContainers) {
            var mod = container.getProvider();
            var modMeta = mod.getMetadata();
            Solstice.LOGGER.info("Registering module provider '{}' ({}) v{}", modMeta.getName(), modMeta.getId(), modMeta.getVersion());
            try {
                var provider = container.getEntrypoint();
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
                Solstice.LOGGER.error("Error registering a module from {}", modMeta.getId(), e);
            }
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

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        for (var module : modules) {
            for (var command : module.getCommands()) {
                command.register(dispatcher, commandRegistry, environment);
            }
        }
    }
}
