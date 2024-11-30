package me.alexdevs.solstice.commands.spawn;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("spawn")
                .requires(Permissions.require("solstice.command.spawn", true))
                .executes(context -> execute(context, null))
                .then(argument("players", EntityArgumentType.players())
                        .requires(Permissions.require("solstice.command.spawn.other", 2))
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "players"))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        if(players == null) {
            var player = context.getSource().getPlayerOrThrow();
            sendToSpawn(context, player);
            return 1;
        } else {
            for(ServerPlayerEntity player : players) {
                sendToSpawn(context, player);
                context.getSource().sendFeedback(() -> Text.literal("Sent ").append(player.getDisplayName()).append(" to spawn."), true);
            }
            return players.size();
        }
    }

    private static void sendToSpawn(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        var serverState = Solstice.state.getServerState();
        var playerContext = PlaceholderContext.of(player);
        var spawnPosition = serverState.spawn;
        if (spawnPosition == null) {
            var server = context.getSource().getServer();
            var spawnPos = server.getOverworld().getSpawnPos();
            spawnPosition = new ServerPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0, server.getOverworld());
        }

        context.getSource().sendFeedback(() -> Format.parse(
                Solstice.locale().commands.spawn.teleporting,
                playerContext
        ), false);
        spawnPosition.teleport(player);
    }
}
