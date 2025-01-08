package me.alexdevs.solstice.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface WorldSaveCallback {
    Event<WorldSaveCallback> EVENT = EventFactory.createArrayBacked(WorldSaveCallback.class, (callbacks) ->
            (server, suppressLogs, flush, force) -> {
                for (WorldSaveCallback callback : callbacks) {
                    callback.onSave(server, suppressLogs, flush, force);
                }

            });

    void onSave(MinecraftServer server, boolean suppressLogs, boolean flush, boolean force);
}