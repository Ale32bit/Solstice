package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TeamFormatter {
    public static boolean sendTeamMessage(ServerPlayer receiver, PlayerChatMessage message, ChatType.Bound params, @Nullable ServerPlayer sender) {
        if (sender == null) {
            return false;
        }

        var team = params.targetName().orElse(null);
        if (team == null) {
            return false;
        }

        var config = ModuleProvider.STYLING.getConfig();
        var isSent = params.chatType().value().chat().translationKey().equals("chat.type.team.sent");
        var format = isSent ? config.teamSentFormat : config.teamFormat;

        if (format.isBlank()) {
            return false;
        }

        var playerContext = PlaceholderContext.of(sender);
        var messageText = Components.chat(message, sender);

        var text = Format.parse(
                format,
                playerContext,
                Map.of(
                        "team", team,
                        "sender", params.name(),
                        "message", messageText
                )
        );

        receiver.sendSystemMessage(text);
        return true;
    }
}
