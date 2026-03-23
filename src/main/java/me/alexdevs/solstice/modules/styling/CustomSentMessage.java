package me.alexdevs.solstice.modules.styling;

import me.alexdevs.solstice.modules.ModuleProvider;
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

    class Chat implements OutgoingChatMessage {
        private final PlayerChatMessage message;

        @Nullable
        private final ServerPlayer sender;

        private final Component formattedChatMessage;

        public Chat(PlayerChatMessage message, @Nullable ServerPlayer sender) {
            this.message = message;
            this.sender = sender;

            // Instead of building the message for every single player in the server, we create a cache of it.
            // The chance that this is used as a chat message is much, much higher than the other cases (me, team msg).
            formattedChatMessage = ChatFormatter.getFormattedMessage(message, sender);
        }

        @Override
        public Component content() {
            return this.message.decoratedContent();
        }

        @Override
        public void sendToPlayer(ServerPlayer receiver, boolean filterMaskEnabled, ChatType.Bound params) {
            var ignoreModule = ModuleProvider.IGNORE;
            if (ignoreModule.isEnabled() && ignoreModule.isIgnoring(receiver, sender)) {
                return;
            }

            PlayerChatMessage signedMessage = this.message.filter(filterMaskEnabled);
            if (signedMessage.isFullyFiltered()) {
                return;
            }

            //? if >= 1.21.1 {
            /*switch (params.chatType().value().chat().translationKey()) {*/
            //? } else {
            switch (params.chatType().chat().translationKey()) {
            //? }
                case "chat.type.text":
                    receiver.sendSystemMessage(this.formattedChatMessage);
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
