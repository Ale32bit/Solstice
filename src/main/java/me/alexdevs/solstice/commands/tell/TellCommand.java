package me.alexdevs.solstice.commands.tell;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.util.Components;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class TellCommand {
    public static final HashMap<String, String> lastSender = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var messageNode = dispatcher.register(literal("msg")
                .requires(Permissions.require("solstice.command.tell", true))
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var playerManager = context.getSource().getServer().getPlayerManager();
                            return CommandSource.suggestMatching(
                                    playerManager.getPlayerNames(),
                                    builder);
                        })
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(TellCommand::execute))));

        dispatcher.register(literal("tell").redirect(messageNode));
        dispatcher.register(literal("w").redirect(messageNode));
        dispatcher.register(literal("dm").redirect(messageNode));
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        var targetName = StringArgumentType.getString(context, "player");
        var message = StringArgumentType.getString(context, "message");

        sendDirectMessage(targetName, source, message);
        return 1;
    }

    public static void sendDirectMessage(String targetName, ServerCommandSource source, String message) {
        Text targetDisplayName;
        ServerPlayerEntity targetPlayer = null;
        if (targetName.equalsIgnoreCase("server")) {
            targetDisplayName = Text.of("Server");
        } else {
            targetPlayer = source.getServer().getPlayerManager().getPlayer(targetName);
            if (targetPlayer == null) {
                var placeholders = Map.of(
                        "targetPlayer", Text.of(targetName)
                );
                var sourceContext = PlaceholderContext.of(source);

                source.sendFeedback(() -> Format.parse(
                        Solstice.locale().commands.tell.playerNotFound,
                        sourceContext,
                        placeholders
                ), false);
                return;
            }
            targetDisplayName = targetPlayer.getDisplayName();
        }

        var parsedMessage = Components.chat(message, source);

        var serverContext = PlaceholderContext.of(source.getServer());
        var sourceContext = PlaceholderContext.of(source);
        PlaceholderContext targetContext;
        if (targetPlayer == null) {
            targetContext = serverContext;
        } else {
            targetContext = PlaceholderContext.of(targetPlayer);
        }


        var you = Format.parse(Solstice.locale().commands.tell.you);

        var placeholdersToSource = Map.of(
                "sourcePlayer", you,
                "targetPlayer", targetDisplayName,
                "message", parsedMessage
        );

        var placeholdersToTarget = Map.of(
                "sourcePlayer", source.getDisplayName(),
                "targetPlayer", you,
                "message", parsedMessage
        );

        var placeholders = Map.of(
                "sourcePlayer", source.getDisplayName(),
                "targetPlayer", targetDisplayName,
                "message", parsedMessage
        );

        var sourceText = Format.parse(
                Solstice.locale().commands.tell.message,
                sourceContext,
                placeholdersToSource
        );
        var targetText = Format.parse(
                Solstice.locale().commands.tell.message,
                targetContext,
                placeholdersToTarget
        );
        var genericText = Format.parse(
                Solstice.locale().commands.tell.message,
                serverContext,
                placeholders
        );
        var spyText = Format.parse(
                Solstice.locale().commands.tell.messageSpy,
                serverContext,
                placeholders
        );

        lastSender.put(targetName, source.getName());
        lastSender.put(source.getName(), targetName);

        if (!source.getName().equals(targetName)) {
            source.sendMessage(sourceText);
        }
        if (targetPlayer != null) {
            targetPlayer.sendMessage(targetText);
            if (source.isExecutedByPlayer()) {
                source.getServer().sendMessage(genericText);
            }
        } else {
            // avoid duped message
            source.getServer().sendMessage(targetText);
        }

        var lp = Solstice.getInstance().luckPerms();
        var playerAdapter = lp.getPlayerAdapter(ServerPlayerEntity.class);
        source.getServer().getPlayerManager().getPlayerList().forEach(player -> {
            var playerName = player.getGameProfile().getName();
            if (playerName.equals(targetName) || playerName.equals(source.getName())) {
                return;
            }
            var playerPerms = playerAdapter.getPermissionData(player);
            if (playerPerms.checkPermission("solstice.tell.spy").asBoolean()) {
                player.sendMessage(spyText);
            }
        });
    }
}
