package me.alexdevs.solstice.modules.styling;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.styling.formatters.ChatFormatter;
import me.alexdevs.solstice.modules.styling.formatters.EmoteFormatter;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface CustomSentMessage extends OutgoingChatMessage {

    static OutgoingChatMessage of(PlayerChatMessage message, @Nullable ServerPlayer sender) {
        if (message.isSystem() && sender == null) {
            return new Profileless(message.decoratedContent());
        }
        return new Chat(message, sender);
    }

    record Profileless(Component getContent) implements OutgoingChatMessage {
        @Override
        public Component content() {
            return getContent;
        }

        @Override
        public void sendToPlayer(ServerPlayer sender, boolean filterMaskEnabled, ChatType.Bound params) {
            sender.connection.sendDisguisedChatMessage(this.getContent, params);
        }
    }

    record Chat(PlayerChatMessage message, ServerPlayer sender) implements OutgoingChatMessage {
        @Override
        public Component content() {
            return this.message.decoratedContent();
        }

        @Override
        public void sendToPlayer(ServerPlayer receiver, boolean filterMaskEnabled, ChatType.Bound params) {
            var ignoreModule = Solstice.modules.getModule(IgnoreModule.class);
            if (ignoreModule.isEnabled() && ignoreModule.isIgnoring(receiver, sender)) {
                return;
            }
            PlayerChatMessage signedMessage = this.message.filter(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                switch (params.chatType().chat().translationKey()) {
                    case "chat.type.text":
                        ChatFormatter.sendChatMessage(receiver, message, params, sender);
                        break;
                    case "chat.type.emote":
                        EmoteFormatter.sendEmoteMessage(receiver, message, params, sender);
                        break;
                    default:
                        receiver.connection.sendDisguisedChatMessage(this.message.decoratedContent(), params);
                        break;
                }
            }

        }
    }
}
