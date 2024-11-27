package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.ServerPosition;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerTeleport {
    Event<PlayerTeleport> EVENT = EventFactory.createArrayBacked(PlayerTeleport.class,
            (listeners) -> (player, origin, destination) -> {
                for (PlayerTeleport listener : listeners) {
                    listener.teleport(player, origin, destination);
                }
            });

    void teleport(ServerPlayerEntity player, ServerPosition origin, ServerPosition destination);
}
