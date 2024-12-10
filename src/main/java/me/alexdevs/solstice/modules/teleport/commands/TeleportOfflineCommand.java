package me.alexdevs.solstice.modules.teleport.commands;

import me.alexdevs.solstice.Solstice;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportOfflineCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var node = literal("tpoffline")
                .requires(Permissions.require("solstice.command.tpoffline", 2))
                .then(argument("player", StringArgumentType.word())
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrThrow();
                            var server = context.getSource().getServer();

                            var targetName = StringArgumentType.getString(context, "player");

                            server.getUserCache().findByNameAsync(targetName, gameProfile -> {
                                if (gameProfile.isEmpty()) {
                                    source.sendFeedback(() -> Text.literal("Could not find player").formatted(Formatting.RED), false);
                                    return;
                                }

                                var targetState = Solstice.state.getPlayerState(gameProfile.get());
                                if(targetState == null || targetState.logoffPosition == null) {
                                    source.sendFeedback(() -> Text.literal("Could not find location of offline player").formatted(Formatting.RED), false);
                                    return;
                                }

                                source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.single", player.getDisplayName(), Text.of(gameProfile.get().getName())), true);

                                targetState.logoffPosition.teleport(player, true);
                            });
                            return 1;
                        }));

        dispatcher.register(node);
    }
}
