package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerTeleportCallback {
    Event<PlayerTeleportCallback> EVENT = EventFactory.createArrayBacked(PlayerTeleportCallback.class,
            (listeners) -> (player, origin, destination) -> {
                for (PlayerTeleportCallback listener : listeners) {
                    listener.teleport(player, origin, destination);
                }
            });

    void teleport(ServerPlayer player, ServerLocation origin, ServerLocation destination);
}
