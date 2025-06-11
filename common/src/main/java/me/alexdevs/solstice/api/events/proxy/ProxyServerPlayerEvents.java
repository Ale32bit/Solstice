package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public class ProxyServerPlayerEvents {
    /**
     * An event that is called after a player has been respawned.
     *
     * <p>Mods may use this event for reference clean up on the old player.
     */
    public static final Event<AfterRespawn> AFTER_RESPAWN = EventFactory.createArrayBacked(
            ProxyServerPlayerEvents.AfterRespawn.class, callbacks -> (oldPlayer, newPlayer, alive) -> {
                for (AfterRespawn callback : callbacks) {
                    callback.afterRespawn(oldPlayer, newPlayer, alive);
                }
            }
    );

    @FunctionalInterface
    public interface AfterRespawn {
        /**
         * Called after player a has been respawned.
         *
         * @param oldPlayer the old player
         * @param newPlayer the new player
         * @param alive     whether the old player is still alive
         */
        void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive);
    }
}
