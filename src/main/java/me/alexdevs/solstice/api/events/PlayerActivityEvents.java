package me.alexdevs.solstice.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerActivityEvents {

    public static final Event<PlayerActivityEvents.Afk> AFK = EventFactory.createArrayBacked(PlayerActivityEvents.Afk.class, callbacks -> (handler, server) -> {
        for (PlayerActivityEvents.Afk callback : callbacks) {
            callback.onAfk(handler, server);
        }
    });

    public static final Event<PlayerActivityEvents.AfkReturn> AFK_RETURN = EventFactory.createArrayBacked(PlayerActivityEvents.AfkReturn.class, callbacks -> (handler, server) -> {
        for (PlayerActivityEvents.AfkReturn callback : callbacks) {
            callback.onAfkReturn(handler, server);
        }
    });

    @FunctionalInterface
    public interface Afk {
        void onAfk(ServerPlayerEntity player, MinecraftServer server);
    }

    @FunctionalInterface
    public interface AfkReturn {
        void onAfkReturn(ServerPlayerEntity player, MinecraftServer server);
    }
}
