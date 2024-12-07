package me.alexdevs.solstice.commands.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("homes")
                .requires(Permissions.require("solstice.command.homes", true))
                .executes(HomesCommand::execute)
                .then(argument("player", GameProfileArgumentType.gameProfile())
                        .requires(Permissions.require("solstice.command.homes.others", 2))
                        .executes(context -> executeOthers(context, GameProfileArgumentType.getProfileArgument(context, "player"))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
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
    }

    private static int executeOthers(CommandContext<ServerCommandSource> context, Collection<GameProfile> profiles) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();

        if(profiles.size() > 1) {

            return 0;
        }

        var profile = profiles.iterator().next();

        var playerState = Solstice.state.getPlayerState(profile);
        var homeList = playerState.homes.keySet().stream().toList();
        var playerContext = PlaceholderContext.of(player);

        if(homeList.isEmpty()) {
            var placeholders = Map.of(
                    "owner", Text.of(profile.getName())
            );
            context.getSource().sendFeedback(() -> Format.parse(
                    Solstice.locale().commands.home.noHomesOther,
                    playerContext,
                    placeholders
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
                    "home", Text.of(homeList.get(i)),
                    "owner", Text.of(profile.getName())
            );

            listText = listText.append(Format.parse(
                    Solstice.locale().commands.home.homesFormatOther,
                    playerContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "homeList", listText,
                "owner", Text.of(profile.getName())
        );
        context.getSource().sendFeedback(() -> Format.parse(
                Solstice.locale().commands.home.homeListOther,
                playerContext,
                placeholders
        ), false);

        return 1;
    }
}