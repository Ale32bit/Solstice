package me.alexdevs.solstice.commands.moderation;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class IgnoreListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("ignorelist")
                .requires(Permissions.require("solstice.command.ignorelist", true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var playerState = Solstice.state.getPlayerState(player);
                    var ignoreList = playerState.ignoredPlayers;
                    var playerContext = PlaceholderContext.of(player);

                    if(ignoreList.isEmpty()) {
                        context.getSource().sendFeedback(() -> Format.parse(
                                Solstice.locale().commands.ignore.ignoreListEmpty,
                                playerContext
                        ), false);
                        return 1;
                    }

                    var listText = Text.empty();
                    var comma = Format.parse(Solstice.locale().commands.ignore.ignoreListComma);
                    for(var i = 0; i < ignoreList.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }

                        String playerName;
                        var gameProfile = context.getSource().getServer().getUserCache().getByUuid(ignoreList.get(i));
                        if (gameProfile.isPresent()) {
                            playerName = gameProfile.get().getName();
                        } else {
                            playerName = ignoreList.get(i).toString();
                        }

                        var placeholders = Map.of(
                                "player", Text.of(playerName)
                        );

                        listText = listText.append(Format.parse(
                                Solstice.locale().commands.ignore.ignoreListFormat,
                                playerContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "playerList", (Text) listText
                    );
                    context.getSource().sendFeedback(() -> Format.parse(
                            Solstice.locale().commands.ignore.ignoreList,
                            playerContext,
                            placeholders
                    ), false);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
