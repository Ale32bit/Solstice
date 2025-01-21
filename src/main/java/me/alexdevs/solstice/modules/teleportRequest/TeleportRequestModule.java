package me.alexdevs.solstice.modules.teleportRequest;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportAcceptCommand;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportAskCommand;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportAskHereCommand;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportDenyCommand;
import me.alexdevs.solstice.modules.teleportRequest.data.TeleportConfig;
import me.alexdevs.solstice.modules.teleportRequest.data.TeleportLocale;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TeleportRequestModule extends ModuleBase.Toggleable {
    public static final String ID = "teleportrequest";

    public final ConcurrentHashMap<UUID, ConcurrentLinkedDeque<TeleportRequest>> teleportRequests = new ConcurrentHashMap<>();

    public TeleportRequestModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, TeleportConfig.class, TeleportConfig::new);
        Solstice.localeManager.registerModule(ID, TeleportLocale.MODULE);

        commands.add(new TeleportAcceptCommand(this));
        commands.add(new TeleportAskCommand(this));
        commands.add(new TeleportAskHereCommand(this));
        commands.add(new TeleportDenyCommand(this));

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
