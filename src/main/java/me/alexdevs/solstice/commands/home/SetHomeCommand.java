package me.alexdevs.solstice.commands.home;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.util.Components;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("sethome")
                .requires(Permissions.require("solstice.command.sethome", true))
                .executes(context -> execute(context,
                        "home",
                        false))
                .then(argument("name", StringArgumentType.word())
                        .executes(context -> execute(context,
                                StringArgumentType.getString(context, "name"),
                                false))
                        .then(argument("force", BoolArgumentType.bool())
                                .executes(context -> execute(context,
                                        StringArgumentType.getString(context, "name"),
                                        BoolArgumentType.getBool(context, "force")))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, String name, boolean forced) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var playerState = Solstice.state.getPlayerState(player.getUuid());
        var homes = playerState.homes;
        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "home", Text.of(name),
                "forceSetButton", Components.button(
                        Solstice.locale().commands.home.forceSetLabel,
                        Solstice.locale().commands.home.forceSetHover,
                        "/sethome " + name + " true"
                )
        );

        var exists = homes.containsKey(name);
        if (exists && !forced) {
            var text = Format.parse(
                    Solstice.locale().commands.home.homeExists,
                    playerContext,
                    placeholders
            );

            context.getSource().sendFeedback(() -> text, false);

            return 1;
        }

        var maxHomes = Solstice.config().homes.maxHomes;
        if(maxHomes >= 0 && homes.size() >= maxHomes && !exists) {
            context.getSource().sendFeedback(() -> Format.parse(
                    Solstice.locale().commands.home.maxHomesReached,
                    playerContext,
                    placeholders
            ), false);
            return 1;
        }

        var homePosition = new ServerPosition(player);
        homes.put(name, homePosition);

        Solstice.state.savePlayerState(player.getUuid(), playerState);

        context.getSource().sendFeedback(() -> Format.parse(
                Solstice.locale().commands.home.homeSetSuccess,
                playerContext,
                placeholders
        ), false);

        return 1;
    }
}
