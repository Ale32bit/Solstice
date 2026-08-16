package me.alexdevs.solstice.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

public interface AllowP2PMessageCallback {
    Event<AllowP2PMessageCallback> ALLOW_MESSAGE = EventFactory.createArrayBacked(AllowP2PMessageCallback.class, callbacks ->
            (player, receiver, message) -> {
                for (var callback : callbacks) {
                    if (!callback.allowMessage(player, receiver, message))
                        return false;
                }
                return true;
            });

    boolean allowMessage(ServerPlayer player, ServerPlayer receiver, PlayerChatMessage message);
}
