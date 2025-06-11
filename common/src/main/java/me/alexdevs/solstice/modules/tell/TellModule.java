package me.alexdevs.solstice.modules.tell;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.permissions.Permissions;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.notifications.NotificationsModule;
import me.alexdevs.solstice.modules.tell.commands.ReplyCommand;
import me.alexdevs.solstice.modules.tell.commands.TellCommand;
import me.alexdevs.solstice.modules.tell.data.TellLocale;
import me.alexdevs.solstice.api.text.Components;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class TellModule extends ModuleBase.Toggleable {
    public static final String ID = "tell";
    public final HashMap<String, String> lastSender = new HashMap<>();

    public TellModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, TellLocale.MODULE);

        commands.add(new TellCommand(this));
        commands.add(new ReplyCommand(this));
    }

    public void sendDirectMessage(String targetName, CommandSourceStack source, String message) {
        var locale = Solstice.localeManager.getLocale(ID);
        Component targetDisplayName;
        ServerPlayer targetPlayer = null;
        if (targetName.equalsIgnoreCase("server")) {
            targetDisplayName = Component.nullToEmpty("Server");
        } else {
            targetPlayer = source.getServer().getPlayerList().getPlayerByName(targetName);
            if (targetPlayer == null) {
                var placeholders = Map.of(
                        "targetPlayer", Component.nullToEmpty(targetName)
                );
                var sourceContext = PlaceholderContext.of(source);

                source.sendSuccess(() -> locale.get(
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

        lastSender.put(targetName, source.getTextName());
        lastSender.put(source.getTextName(), targetName);

        if (!source.getTextName().equals(targetName)) {
            source.sendSystemMessage(sourceText);
        }
        if (targetPlayer != null) {
            var ignoreModule = Solstice.modules.getModule(IgnoreModule.class);
            if (!source.isPlayer() || !ignoreModule.isIgnoring(targetPlayer, source.getPlayer())) {
                targetPlayer.sendSystemMessage(targetText);
                NotificationsModule.notify(targetPlayer);
            }

            if (source.isPlayer()) {
                source.getServer().sendSystemMessage(genericText);
            }
        } else {
            // avoid duped message
            source.getServer().sendSystemMessage(targetText);
        }

        source.getServer().getPlayerList().getPlayers().forEach(player -> {
            var playerName = player.getGameProfile().getName();
            if (playerName.equals(targetName) || playerName.equals(source.getTextName())) {
                return;
            }
            if (Permissions.check(player, getPermissionNode("spy"))) {
                player.sendSystemMessage(spyText);
            }
        });
    }
}
