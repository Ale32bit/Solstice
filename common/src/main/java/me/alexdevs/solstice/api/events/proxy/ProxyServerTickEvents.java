package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.server.MinecraftServer;

public class ProxyServerTickEvents {
    public static final Event<Begin> START_SERVER_TICK = EventFactory.createArrayBacked(
            Begin.class, callbacks -> (server) -> {
                for (var callback : callbacks) {
                    callback.onStartTick(server);
                }
            }
    );

    public static final Event<End> END_SERVER_TICK = EventFactory.createArrayBacked(
            End.class, callbacks -> (server) -> {
                for (var callback : callbacks) {
                    callback.onEndTick(server);
                }
            }
    );

    @FunctionalInterface
    public interface Begin {
        void onStartTick(MinecraftServer server);
    }

    @FunctionalInterface
    public interface End {
        void onEndTick(MinecraftServer server);
    }
}
