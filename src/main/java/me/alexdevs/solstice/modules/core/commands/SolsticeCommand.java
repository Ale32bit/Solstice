package me.alexdevs.solstice.modules.core.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.api.module.CommandProvider;
import me.alexdevs.solstice.modules.core.CoreModule;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class SolsticeCommand extends CommandProvider<CoreModule> {
    public SolsticeCommand(CoreModule module, CommandContext<ServerCommandSource> context, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(module, context, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("solstice", "sol");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(this.require());
    }
}
