package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.server.MinecraftServer;

public class ProxyServerLifecycleEvents {
    public static final Event<Starting> SERVER_STARTING = EventFactory.createArrayBacked(
            Starting.class, callbacks -> (server) -> {
                for (var callback : callbacks) {
                    callback.onServerStarting(server);
                }
            }
    );

    public static final Event<Started> SERVER_STARTED = EventFactory.createArrayBacked(
            Started.class, callbacks -> (server) -> {
                for (var callback : callbacks) {
                    callback.onServerStarted(server);
                }
            }
    );

    public static final Event<Stopping> SERVER_STOPPING = EventFactory.createArrayBacked(
            Stopping.class, callbacks -> (server) -> {
                for (var callback : callbacks) {
                    callback.onServerStopping(server);
                }
            }
    );

    public static final Event<Stopped> SERVER_STOPPED = EventFactory.createArrayBacked(
            Stopped.class, callbacks -> (server) -> {
                for (var callback : callbacks) {
                    callback.onServerStopped(server);
                }
            }
    );

    @FunctionalInterface
    public interface Starting {
        void onServerStarting(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Started {
        void onServerStarted(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopping {
        void onServerStopping(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopped {
        void onServerStopped(MinecraftServer server);
    }
}
