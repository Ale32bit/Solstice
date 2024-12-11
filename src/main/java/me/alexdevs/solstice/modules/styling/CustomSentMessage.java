package me.alexdevs.solstice.modules.styling;

import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.modules.styling.formatters.ChatFormatter;
import me.alexdevs.solstice.modules.styling.formatters.EmoteFormatter;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface CustomSentMessage extends SentMessage {

    static SentMessage of(SignedMessage message, @Nullable ServerPlayerEntity sender) {
        if (message.isSenderMissing() && sender == null) {
            return new Profileless(message.getContent());
        }
        return new Chat(message, sender);
    }

    record Profileless(Text getContent) implements SentMessage {
        @Override
        public Text content() {
            return getContent;
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            sender.networkHandler.sendProfilelessChatMessage(this.getContent, params);
        }
    }

    record Chat(SignedMessage message, ServerPlayerEntity sender) implements SentMessage {
        @Override
        public Text content() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity receiver, boolean filterMaskEnabled, MessageType.Parameters params) {
            if (ModerationModule.isIgnoring(receiver, sender)) {
                return;
            }
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                switch (params.type().value().chat().translationKey()) {
                    case "chat.type.text":
                        ChatFormatter.sendChatMessage(receiver, message, params, sender);
                        break;
                    case "chat.type.emote":
                        EmoteFormatter.sendEmoteMessage(receiver, message, params, sender);
                        break;
                    default:
                        receiver.networkHandler.sendProfilelessChatMessage(this.message.getContent(), params);
                        break;
                }
            }

        }
    }
}
