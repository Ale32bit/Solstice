package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ProxyServerPlayConnectionEvents {
    public static final Event<Join> JOIN = EventFactory.createArrayBacked(
            Join.class, callbacks -> (player, server) -> {
                for (Join callback : callbacks) {
                    callback.onJoin(player, server);
                }
            }
    );

    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(
            Disconnect.class, callbacks -> (player, server) -> {
                for (Disconnect callback : callbacks) {
                    callback.onDisconnect(player, server);
                }
            }
    );

    @FunctionalInterface
    public interface Join {
        void onJoin(ServerPlayer player, MinecraftServer server);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onDisconnect(ServerPlayer player, MinecraftServer server);
    }
}
