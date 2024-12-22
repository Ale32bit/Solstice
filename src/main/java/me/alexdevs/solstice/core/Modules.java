package me.alexdevs.solstice.core;

import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.api.events.ModuleRegistrationCallback;
import me.alexdevs.solstice.api.module.ModuleBase;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Modules {

    private HashSet<? extends ModuleBase> modules = new HashSet<>();

    public Modules() {
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
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

    public void register() {
        modules = ModuleRegistrationCallback.EVENT.invoker().register();
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        for (var module : modules) {
            for (var command : module.getCommands()) {
                command.register(dispatcher, commandRegistry, environment);
            }
        }
    }
}
