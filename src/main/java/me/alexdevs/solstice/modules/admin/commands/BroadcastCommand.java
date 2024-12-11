package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.util.Format;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BroadcastCommand extends ModCommand {
    public BroadcastCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("broadcast");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            var message = StringArgumentType.getString(context, "message");
                            var serverContext = PlaceholderContext.of(context.getSource().getServer());

                            Solstice.getInstance().broadcast(Format.parse(message, serverContext));

                            return 1;
                        }));

    }
}
