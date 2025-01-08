package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.ServerPosition;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerTeleportCallback {
    Event<PlayerTeleportCallback> EVENT = EventFactory.createArrayBacked(PlayerTeleportCallback.class,
            (listeners) -> (player, origin, destination) -> {
                for (PlayerTeleportCallback listener : listeners) {
                    listener.teleport(player, origin, destination);
                }
            });

    void teleport(ServerPlayerEntity player, ServerPosition origin, ServerPosition destination);
}
