package me.alexdevs.solstice.modules.teleport;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TeleportModule {
    public static final String ID = "teleport";

    public static final ConcurrentHashMap<UUID, ConcurrentLinkedDeque<TeleportRequest>> teleportRequests = new ConcurrentHashMap<>();

    public TeleportModule() {
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

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> teleportRequests.remove(handler.getPlayer().getUuid()));
    }

}
