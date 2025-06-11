package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

public class ProxyServerMessageEvents {
    /**
     * An event triggered when the server broadcasts a chat message sent by a player,
     * typically from a client GUI or a player-executed command. Mods can use this to block
     * the message.
     *
     * <p>If a listener returned {@code false}, the message will not be broadcast,
     * the remaining listeners will not be called (if any), and {@link #CHAT_MESSAGE}
     * event will not be triggered.
     */
    public static final Event<AllowChatMessage> ALLOW_CHAT_MESSAGE = EventFactory.createArrayBacked(
            AllowChatMessage.class, handlers -> (message, sender, params) -> {
                for (AllowChatMessage handler : handlers) {
                    if (!handler.allowChatMessage(message, sender, params)) return false;
                }

                return true;
            }
    );

    /**
     * An event triggered when the server broadcasts a chat message sent by a player, typically
     * from a client GUI or a player-executed command. Is not called when {@linkplain
     * #ALLOW_CHAT_MESSAGE chat messages are blocked}.
     */
    public static final Event<ChatMessage> CHAT_MESSAGE = EventFactory.createArrayBacked(
            ChatMessage.class, handlers -> (message, sender, params) -> {
                for (ChatMessage handler : handlers) {
                    handler.onChatMessage(message, sender, params);
                }
            }
    );

    @FunctionalInterface
    public interface AllowChatMessage {
        /**
         * Called when the server broadcasts a chat message sent by a player, typically
         * from a client GUI or a player-executed command. Returning {@code false}
         * prevents the message from being broadcast and the {@link #CHAT_MESSAGE} event
         * from triggering.
         *
         * @param message the broadcast message with message decorators applied; use {@code message.getContent()} to get the text
         * @param sender  the player that sent the message
         * @param params  the {@link ChatType.Bound}
         *
         * @return {@code true} if the message should be broadcast, otherwise {@code false}
         */
        boolean allowChatMessage(PlayerChatMessage message, ServerPlayer sender, ChatType.Bound params);
    }

    @FunctionalInterface
    public interface ChatMessage {
        /**
         * Called when the server broadcasts a chat message sent by a player, typically
         * from a client GUI or a player-executed command. Is not called when {@linkplain
         * #ALLOW_CHAT_MESSAGE chat messages are blocked}.
         *
         * @param message the broadcast message with message decorators applied; use {@code message.getContent()} to get the text
         * @param sender  the player that sent the message
         * @param params  the {@link ChatType.Bound}
         */
        void onChatMessage(PlayerChatMessage message, ServerPlayer sender, ChatType.Bound params);
    }
}
