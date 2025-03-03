package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.ServerLocation;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
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
