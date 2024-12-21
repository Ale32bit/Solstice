package me.alexdevs.solstice.modules.teleportRequest;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportRequest.commands.*;
import me.alexdevs.solstice.modules.teleportRequest.data.TeleportConfig;
import me.alexdevs.solstice.modules.teleportRequest.data.TeleportLocale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TeleportRequestModule extends ModuleBase {
    public static final String ID = "teleportRequest";

    public final ConcurrentHashMap<UUID, ConcurrentLinkedDeque<TeleportRequest>> teleportRequests = new ConcurrentHashMap<>();

    public TeleportRequestModule() {
        super(ID);

        Solstice.configManager.registerData(ID, TeleportConfig.class, TeleportConfig::new);
        Solstice.localeManager.registerModule(ID, TeleportLocale.MODULE);

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

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return List.of();
    }
}
