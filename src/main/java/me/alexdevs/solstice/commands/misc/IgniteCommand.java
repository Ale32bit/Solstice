package me.alexdevs.solstice.commands.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IgniteCommand {
    public static final int defaultTicks = 200; // 10 seconds

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(command("ignite"));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> command(String command) {
        return literal(command)
                .requires(Permissions.require("solstice.command.ignite", 2))
                .executes(context -> execute(context, null, null))
                .then(argument("players", EntityArgumentType.players())
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "players"), null))
                        .then(argument("ticks", IntegerArgumentType.integer(0))
                                .executes(context ->
                                        execute(context, EntityArgumentType.getPlayers(context, "players"), IntegerArgumentType.getInteger(context, "ticks"))
                                )
                        )
                );
    }

    private static int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> players, @Nullable Integer ticks) throws CommandSyntaxException {
        var source = context.getSource();
        if (players == null) {
            ignite(source, source.getPlayerOrThrow(), ticks);
            return 1;
        } else {
            for (ServerPlayerEntity player : players) {
                ignite(source, player, ticks);
            }

            return players.size();
        }
    }

    private static void ignite(ServerCommandSource source, ServerPlayerEntity player, @Nullable Integer ticks) {
        if (ticks == null) {
            player.setFireTicks(defaultTicks);
            source.sendFeedback(() -> Text.literal("Ignited ").append(source.getDisplayName()), true);
        } else {
            player.setFireTicks(ticks);
            source.sendFeedback(() -> Text.literal("Ignited ").append(source.getDisplayName()).append(Text.of(String.format(" for %d ticks", ticks))), true);
        }
    }
}
