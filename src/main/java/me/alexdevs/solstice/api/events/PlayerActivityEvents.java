package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.modules.afk.AfkModule;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

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
        void onAfk(ServerPlayerEntity player);
    }

    @FunctionalInterface
    public interface AfkReturn {
        void onAfkReturn(ServerPlayerEntity player, AfkModule.AfkTriggerReason reason);
    }
}
