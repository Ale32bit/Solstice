package me.alexdevs.solstice.commands.home;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class HomesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("homes")
                .requires(Permissions.require("solstice.command.homes", true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var playerState = Solstice.state.getPlayerState(player);
                    var homeList = playerState.homes.keySet().stream().toList();
                    var playerContext = PlaceholderContext.of(player);

                    if(homeList.isEmpty()) {
                        context.getSource().sendFeedback(() -> Format.parse(
                                Solstice.locale().commands.home.noHomes,
                                playerContext
                        ), false);
                        return 1;
                    }

                    var listText = Text.empty();
                    var comma = Format.parse(Solstice.locale().commands.home.homesComma);
                    for(var i = 0; i < homeList.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }
                        var placeholders = Map.of(
                                "home", Text.of(homeList.get(i))
                        );

                        listText = listText.append(Format.parse(
                                Solstice.locale().commands.home.homesFormat,
                                playerContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "homeList", (Text) listText
                    );
                    context.getSource().sendFeedback(() -> Format.parse(
                            Solstice.locale().commands.home.homeList,
                            playerContext,
                            placeholders
                    ), false);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}