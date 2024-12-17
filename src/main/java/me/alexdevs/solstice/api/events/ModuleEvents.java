package me.alexdevs.solstice.api.events;

import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.api.module.ModCommand;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

public class ModuleEvents {
    public static final Event<Command> COMMAND = EventFactory.createArrayBacked(Command.class, callbacks -> (dispatcher, registry, environment) -> {
        List<ModCommand<?>> commands = new ArrayList<>();
        for (Command callback : callbacks) {
            commands.addAll(callback.register(dispatcher, registry, environment));
        }
        return commands;
    });

    @FunctionalInterface
    public interface Command {
        List<ModCommand<?>> register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment);
    }
}
