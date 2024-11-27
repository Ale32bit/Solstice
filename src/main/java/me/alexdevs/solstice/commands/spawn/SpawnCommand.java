package me.alexdevs.solstice.commands.spawn;

import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("spawn")
                .requires(Permissions.require("solstice.command.spawn", true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
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

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
