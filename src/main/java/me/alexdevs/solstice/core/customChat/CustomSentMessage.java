package me.alexdevs.solstice.core.customChat;

import me.alexdevs.solstice.Solstice;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public record CustomSentMessage(SignedMessage message) implements SentMessage {

    @Override
    public Text getContent() {
        return this.message.getContent();
    }

    @Override
    public void send(ServerPlayerEntity receiver, boolean filterMaskEnabled, MessageType.Parameters params) {
        SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
        Solstice.LOGGER.info("Message params type: {}", params.type().chat().translationKey());
        if (!signedMessage.isFullyFiltered()) {
            switch (params.type().chat().translationKey()) {
                case "chat.type.text":
                    CustomChatMessage.sendChatMessage(receiver, message, params);
                    break;
                case "chat.type.emote":
                    CustomEmoteMessage.sendEmoteMessage(receiver, message, params);
                    break;
                default:
                    receiver.networkHandler.sendChatMessage(this.message, params);
                    break;
            }
        }
    }

}
