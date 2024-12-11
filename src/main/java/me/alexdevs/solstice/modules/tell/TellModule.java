package me.alexdevs.solstice.modules.tell;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.modules.tell.commands.ReplyCommand;
import me.alexdevs.solstice.modules.tell.commands.TellCommand;
import me.alexdevs.solstice.modules.tell.data.TellLocale;
import me.alexdevs.solstice.util.Components;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class TellModule {
    public static final String ID = "tell";
    public static final String SOCIALSPY_PERMISSION = "solstice.socialspy";
    public final HashMap<String, String> lastSender = new HashMap<>();

    public TellModule() {
        Solstice.localeManager.registerModule(ID, TellLocale.MODULE);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new TellCommand(dispatcher, registryAccess, environment);
            new ReplyCommand(dispatcher, registryAccess, environment);
        });
    }

    public void sendDirectMessage(String targetName, ServerCommandSource source, String message) {
        var locale = Solstice.localeManager.getLocale(ID);
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

                source.sendFeedback(() -> locale.get(
                        "playerNotFound",
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


        var you = locale.get("you");

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

        var sourceText = locale.get(
                "message",
                sourceContext,
                placeholdersToSource
        );
        var targetText = locale.get(
                "message",
                targetContext,
                placeholdersToTarget
        );
        var genericText = locale.get(
                "message",
                serverContext,
                placeholders
        );
        var spyText = locale.get(
                "messageSpy",
                serverContext,
                placeholders
        );

        lastSender.put(targetName, source.getName());
        lastSender.put(source.getName(), targetName);

        if (!source.getName().equals(targetName)) {
            source.sendMessage(sourceText);
        }
        if (targetPlayer != null) {
            if (!source.isExecutedByPlayer() || !ModerationModule.isIgnoring(targetPlayer, source.getPlayer())) {
                targetPlayer.sendMessage(targetText);
            }

            if (source.isExecutedByPlayer()) {
                source.getServer().sendMessage(genericText);
            }
        } else {
            // avoid duped message
            source.getServer().sendMessage(targetText);
        }

        source.getServer().getPlayerManager().getPlayerList().forEach(player -> {
            var playerName = player.getGameProfile().getName();
            if (playerName.equals(targetName) || playerName.equals(source.getName())) {
                return;
            }
            if (Permissions.check(player, SOCIALSPY_PERMISSION)) {
                player.sendMessage(spyText);
            }
        });
    }
}
