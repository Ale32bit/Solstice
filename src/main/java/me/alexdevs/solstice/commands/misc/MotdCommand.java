package me.alexdevs.solstice.commands.misc;

import me.alexdevs.solstice.core.Motd;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class MotdCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("motd")
                .requires(Permissions.require("solstice.command.motd", true))
                .executes(context -> {
                    var sourceContext = PlaceholderContext.of(context.getSource());

                    context.getSource().sendMessage(Motd.buildMotd(sourceContext));

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
