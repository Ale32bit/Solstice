package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand extends ModCommand {
    public SpawnCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    private static int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        if (players == null) {
            var player = context.getSource().getPlayerOrThrow();
            sendToSpawn(context, player);
            return 1;
        } else {
            for (ServerPlayerEntity player : players) {
                sendToSpawn(context, player);
                context.getSource().sendFeedback(() -> Text.literal("Sent ").append(player.getDisplayName()).append(" to spawn."), true);
            }
            return players.size();
        }
    }

    private static void sendToSpawn(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        var serverData = Solstice.serverData.getData(SpawnServerData.class);
        var playerContext = PlaceholderContext.of(player);
        var spawnPosition = serverData.spawn;
        if (spawnPosition == null) {
            var server = context.getSource().getServer();
            var spawnPos = server.getOverworld().getSpawnPos();
            spawnPosition = new ServerPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0, server.getOverworld());
        }

        var locale = Solstice.localeManager.getLocale(SpawnModule.ID);

        context.getSource().sendFeedback(() -> locale.get(
                "teleporting",
                playerContext
        ), false);
        spawnPosition.teleport(player);
    }

    @Override
    public List<String> getNames() {
        return List.of("spawn");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> execute(context, null))
                .then(argument("players", EntityArgumentType.players())
                        .requires(require("other", 2))
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "players"))));
    }
}
