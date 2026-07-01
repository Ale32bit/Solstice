package me.alexdevs.solstice.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public interface HelpOpCallback {
    Event<HelpOpCallback> EVENT = EventFactory.createArrayBacked(HelpOpCallback.class, (callbacks) ->
            (player, message) -> {
                for (HelpOpCallback callback : callbacks) {
                    callback.onHelpRequest(player, message);
                }

            });

    void onHelpRequest(ServerPlayer player, String message);
}