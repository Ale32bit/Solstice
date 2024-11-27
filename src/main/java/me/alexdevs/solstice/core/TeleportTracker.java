package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TeleportTracker {
    public static final ConcurrentHashMap<UUID, ConcurrentLinkedDeque<TeleportRequest>> teleportRequests = new ConcurrentHashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            teleportRequests.forEach((recipient, requestList) -> {
                requestList.forEach(request -> {
                    if (request.remainingTicks-- == 0) {
                        requestList.remove(request);
                    }
                });
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> teleportRequests.put(handler.getPlayer().getUuid(), new ConcurrentLinkedDeque<>()));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> TeleportTracker.teleportRequests.remove(handler.getPlayer().getUuid()));
    }

    public static class TeleportRequest {
        public UUID requestId = UUID.randomUUID();
        public UUID player;
        public UUID target;
        public int remainingTicks;

        public TeleportRequest(UUID player, UUID target) {
            this.player = player;
            this.target = target;
            // Seconds in config per 20 ticks
            this.remainingTicks = Solstice.config().teleportRequests.teleportRequestTimeout * 20;
        }

        public void expire() {
            remainingTicks = 0;
        }
    }
}
