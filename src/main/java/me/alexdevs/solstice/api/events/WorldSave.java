package me.alexdevs.solstice.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface WorldSave {
    Event<WorldSave> EVENT = EventFactory.createArrayBacked(WorldSave.class, (callbacks) ->
            (server, suppressLogs, flush, force) -> {
                for (WorldSave callback : callbacks) {
                    callback.onSave(server, suppressLogs, flush, force);
                }

            });

    void onSave(MinecraftServer server, boolean suppressLogs, boolean flush, boolean force);
}