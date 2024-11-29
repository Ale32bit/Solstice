package me.alexdevs.solstice.commands.moderation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IgnoreCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("ignore")
                .requires(Permissions.require("solstice.command.ignore", true))
                .then(argument("target", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var player = context.getSource().getPlayerOrThrow();
                            var playerManager = context.getSource().getServer().getPlayerManager();
                            return CommandSource.suggestMatching(
                                    Arrays.stream(playerManager.getPlayerNames()).filter(s -> !s.equals(player.getGameProfile().getName())),
                                    builder);
                        })
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();
                            var playerState = Solstice.state.getPlayerState(player);

                            var targetName = StringArgumentType.getString(context, "target");

                            context.getSource().getServer().getUserCache().findByNameAsync(targetName, profileOpt -> {
                                var playerContext = PlaceholderContext.of(player);

                                if (profileOpt.isEmpty()) {
                                    context.getSource().sendFeedback(() -> Format.parse(Solstice.locale().commands.ignore.playerNotFound, playerContext), false);
                                    return;
                                }

                                var profile = profileOpt.get();

                                if (profile.getId().equals(player.getGameProfile().getId())) {
                                    context.getSource().sendFeedback(() -> Format.parse(Solstice.locale().commands.ignore.targetIsSelf, playerContext), false);
                                    return;
                                }

                                var map = Map.of(
                                        "targetName", Text.of(profile.getName())
                                );

                                if (playerState.ignoredPlayers.contains(profile.getId())) {
                                    playerState.ignoredPlayers.remove(profile.getId());
                                    context.getSource().sendFeedback(() -> Format.parse(Solstice.locale().commands.ignore.unblockedPlayer, playerContext, map), false);

                                } else {
                                    playerState.ignoredPlayers.add(profile.getId());
                                    context.getSource().sendFeedback(() -> Format.parse(Solstice.locale().commands.ignore.blockedPlayer, playerContext, map), false);
                                }
                            });

                            return 1;
                        }));

        dispatcher.register(rootCommand);
    }
}
