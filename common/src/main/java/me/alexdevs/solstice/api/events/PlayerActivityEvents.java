package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import me.alexdevs.solstice.modules.afk.AfkModule;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerActivityEvents {

    public static final Event<Afk> AFK = EventFactory.createArrayBacked(Afk.class, callbacks -> (handler) -> {
        for (Afk callback : callbacks) {
            callback.onAfk(handler);
        }
    });

    public static final Event<AfkReturn> AFK_RETURN = EventFactory.createArrayBacked(AfkReturn.class, callbacks -> (handler, reason) -> {
        for (AfkReturn callback : callbacks) {
            callback.onAfkReturn(handler, reason);
        }
    });

    @FunctionalInterface
    public interface Afk {
        void onAfk(ServerPlayer player);
    }

    @FunctionalInterface
    public interface AfkReturn {
        void onAfkReturn(ServerPlayer player, AfkModule.AfkTriggerReason reason);
    }
}
