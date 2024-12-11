package me.alexdevs.solstice.modules.teleport.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.teleport.TeleportModule;
import me.alexdevs.solstice.modules.teleport.TeleportRequest;
import me.alexdevs.solstice.api.ServerPosition;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportAcceptCommand extends ModCommand {
    private final Locale locale = Solstice.localeManager.getLocale(TeleportModule.ID);

    public TeleportAcceptCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpaccept", "tpyes");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal("tpaccept")
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var playerUuid = player.getUuid();
                    var playerRequests = TeleportModule.teleportRequests.get(playerUuid);
                    var playerContext = PlaceholderContext.of(player);

                    var request = playerRequests.pollLast();

                    if (request == null) {
                        context.getSource().sendFeedback(() -> locale.get(
                                "noPending",
                                playerContext
                        ), false);
                        return 1;
                    }

                    execute(context, request);

                    return 1;
                })
                .then(argument("uuid", UuidArgumentType.uuid())
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();
                            var uuid = UuidArgumentType.getUuid(context, "uuid");
                            var playerUuid = player.getUuid();
                            var playerRequests = TeleportModule.teleportRequests.get(playerUuid);
                            var playerContext = PlaceholderContext.of(player);

                            var request = playerRequests.stream().filter(req -> req.requestId.equals(uuid)).findFirst().orElse(null);
                            if (request == null) {
                                context.getSource().sendFeedback(() -> locale.get(
                                        "unavailable",
                                        playerContext
                                ), false);
                                return 1;
                            }

                            execute(context, request);

                            return 1;
                        }));
    }

    private void execute(CommandContext<ServerCommandSource> context, TeleportRequest request) {
        var source = context.getSource();
        request.expire();

        var player = source.getPlayer();

        var playerManager = context.getSource().getServer().getPlayerManager();

        var sourcePlayer = playerManager.getPlayer(request.player);
        var targetPlayer = playerManager.getPlayer(request.target);

        var playerContext = PlaceholderContext.of(player);

        if (sourcePlayer == null || targetPlayer == null) {
            context.getSource().sendFeedback(() -> locale.get(
                    "playerUnavailable",
                    playerContext
            ), false);
            return;
        }

        if (player.getUuid().equals(request.target)) {
            var sourceContext = PlaceholderContext.of(sourcePlayer);
            // accepted a tpa from other to self
            context.getSource().sendFeedback(() -> locale.get(
                    "requestAcceptedResult",
                    playerContext
            ), false);
            sourcePlayer.sendMessage(locale.get(
                    "teleporting",
                    sourceContext
            ), false);
        } else {
            var targetContext = PlaceholderContext.of(targetPlayer);
            // accepted a tpa from self to other
            context.getSource().sendFeedback(() -> locale.get(
                    "teleporting",
                    playerContext
            ), false);

            targetPlayer.sendMessage(locale.get(
                    "requestAccepted",
                    targetContext,
                    Map.of("player", sourcePlayer.getDisplayName())
            ), false);
        }

        var targetPosition = new ServerPosition(targetPlayer);
        targetPosition.teleport(sourcePlayer);
    }
}
