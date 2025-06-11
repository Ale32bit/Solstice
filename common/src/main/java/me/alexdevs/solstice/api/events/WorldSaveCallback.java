package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface WorldSaveCallback {
    Event<WorldSaveCallback> EVENT = EventFactory.createArrayBacked(
            WorldSaveCallback.class, (callbacks) -> (server, suppressLogs, flush, force) -> {
                for (WorldSaveCallback callback : callbacks) {
                    callback.onSave(server, suppressLogs, flush, force);
                }
            }
    );

    void onSave(MinecraftServer server, boolean suppressLogs, boolean flush, boolean force);
}