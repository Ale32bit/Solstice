package me.alexdevs.solstice.modules.near.commands;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NearCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("near")
                .requires(Permissions.require("solstice.command.near", 2))
                .executes(context -> execute(context, Solstice.config().nearCommand.nearCommandDefaultRange, context.getSource().getPlayerOrThrow()))
                .then(argument("radius", IntegerArgumentType.integer(0, Solstice.config().nearCommand.nearCommandMaxRange))
                        .executes(context -> execute(context, IntegerArgumentType.getInteger(context, "radius"), context.getSource().getPlayerOrThrow())));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, int range, ServerPlayerEntity sourcePlayer) {
        var playerContext = PlaceholderContext.of(sourcePlayer);
        var list = new ArrayList<ClosePlayers>();

        var sourcePos = sourcePlayer.getPos();
        sourcePlayer.getServerWorld().getPlayers().forEach(targetPlayer -> {
            var targetPos = targetPlayer.getPos();
            if (!sourcePlayer.getUuid().equals(targetPlayer.getUuid()) && sourcePos.isInRange(targetPos, range)) {
                var distance = sourcePos.distanceTo(targetPos);
                list.add(new ClosePlayers(targetPlayer, distance));
            }
        });

        if (list.isEmpty()) {
            context.getSource().sendFeedback(() -> Format.parse(
                    Solstice.locale().commands.near.noOne,
                    playerContext
            ), false);
            return 1;
        }

        list.sort(Comparator.comparingDouble(ClosePlayers::distance));

        var listText = Text.empty();
        var comma = Format.parse(Solstice.locale().commands.near.comma);
        for (int i = 0; i < list.size(); i++) {
            var player = list.get(i);
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "player", player.player.getDisplayName(),
                    "distance", Text.of(String.format("%.1fm", player.distance))
            );

            var targetContext = PlaceholderContext.of(sourcePlayer);

            listText = listText.append(Format.parse(
                    Solstice.locale().commands.near.format,
                    targetContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "playerList", (Text) listText
        );
        context.getSource().sendFeedback(() -> Format.parse(
                Solstice.locale().commands.near.nearestPlayers,
                playerContext,
                placeholders
        ), false);

        return 1;
    }

    private record ClosePlayers(ServerPlayerEntity player, double distance) {
    }
}
