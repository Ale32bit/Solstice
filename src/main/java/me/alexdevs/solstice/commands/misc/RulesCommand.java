package me.alexdevs.solstice.commands.misc;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.core.InfoPages;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class RulesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("rules")
                .requires(Permissions.require("solstice.command.rules", true))
                .executes(context -> {
                    var sourceContext = PlaceholderContext.of(context.getSource());
                    var rules = InfoPages.getPage("rules", sourceContext);
                    context.getSource().sendFeedback(() -> rules, false);
                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
