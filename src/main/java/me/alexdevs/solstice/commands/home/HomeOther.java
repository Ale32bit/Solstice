package me.alexdevs.solstice.commands.home;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomeOther {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("homeother")
                .requires(Permissions.require("solstice.command.homeother", 2))
                .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> execute(context, "home"))
                        .then(argument("name", StringArgumentType.word())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "name")))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var sourcePlayer = context.getSource().getPlayerOrThrow();
        var profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        var playerContext = PlaceholderContext.of(context.getSource().getPlayer());

        if(profiles.size() > 1) {
            context.getSource().sendFeedback(() -> Format.parse(
                    Solstice.locale().commands.common.tooManyTargets,
                    playerContext
            ), false);
            return 0;
        }

        var profile = profiles.iterator().next();

        var playerState = Solstice.state.getPlayerState(profile);
        var homes = playerState.homes;

        var placeholders = Map.of(
                "home", Text.of(name),
                "owner", Text.of(profile.getName())
        );

        if (!homes.containsKey(name)) {
            context.getSource().sendFeedback(() ->
                    Format.parse(
                            Solstice.locale().commands.home.homeNotFound,
                            playerContext,
                            placeholders
                    ), false);

            return 1;
        }

        context.getSource().sendFeedback(() ->
                Format.parse(
                        Solstice.locale().commands.home.teleportingOther,
                        playerContext,
                        placeholders
                ), true);

        var homePosition = homes.get(name);
        homePosition.teleport(sourcePlayer);

        return 1;
    }
}
