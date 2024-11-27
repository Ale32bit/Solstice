package me.alexdevs.solstice.commands.teleport;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.core.BackTracker;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class BackCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("back")
                .requires(Permissions.require("solstice.command.back", true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var playerContext = PlaceholderContext.of(player);

                    var lastPosition = BackTracker.lastPlayerPositions.get(player.getUuid());
                    if (lastPosition == null) {
                        context.getSource().sendFeedback(() -> Format.parse(
                                Solstice.locale().commands.back.noPosition,
                                playerContext
                        ), false);
                        return 1;
                    }

                    context.getSource().sendFeedback(() -> Format.parse(
                            Solstice.locale().commands.back.teleporting,
                            playerContext
                    ), false);
                    lastPosition.teleport(player);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
