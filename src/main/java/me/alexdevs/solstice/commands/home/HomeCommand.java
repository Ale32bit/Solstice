package me.alexdevs.solstice.commands.home;

import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.Solstice;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("home")
                .requires(Permissions.require("solstice.command.home", true))
                .executes(context -> execute(context, "home"))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isExecutedByPlayer())
                                return CommandSource.suggestMatching(new String[]{}, builder);

                            var playerState = Solstice.state.getPlayerState(context.getSource().getPlayer().getUuid());
                            return CommandSource.suggestMatching(playerState.homes.keySet().stream(), builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var playerState = Solstice.state.getPlayerState(player.getUuid());
        var homes = playerState.homes;
        var playerContext = PlaceholderContext.of(context.getSource().getPlayer());

        var placeholders = Map.of(
                "home", Text.of(name)
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
                        Solstice.locale().commands.home.teleporting,
                        playerContext,
                        placeholders
                ), false);

        var homePosition = homes.get(name);
        homePosition.teleport(player);

        return 1;
    }
}
