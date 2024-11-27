package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.core.BossBarManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public class BossBarEvents {
    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks ->
            (timeBar, server) -> {
                for (Start callback : callbacks) {
                    callback.onStart(timeBar, server);
                }
            });

    public static final Event<End> END = EventFactory.createArrayBacked(End.class, callbacks ->
            (timeBar, server) -> {
                for (End callback : callbacks) {
                    callback.onEnd(timeBar, server);
                }
            });

    public static final Event<Cancel> CANCEL = EventFactory.createArrayBacked(Cancel.class, callbacks ->
            (timeBar, server) -> {
                for (Cancel callback : callbacks) {
                    callback.onCancel(timeBar, server);
                }
            });

    public static final Event<Progress> PROGRESS = EventFactory.createArrayBacked(Progress.class, callbacks ->
            (timeBar, server) -> {
                for (Progress callback : callbacks) {
                    callback.onProgress(timeBar, server);
                }
            });

    @FunctionalInterface
    public interface Start {
        void onStart(BossBarManager.TimeBar timeBar, MinecraftServer server);
    }

    @FunctionalInterface
    public interface End {
        void onEnd(BossBarManager.TimeBar timeBar, MinecraftServer server);
    }

    @FunctionalInterface
    public interface Cancel {
        void onCancel(BossBarManager.TimeBar timeBar, MinecraftServer server);
    }

    @FunctionalInterface
    public interface Progress {
        void onProgress(BossBarManager.TimeBar timeBar, MinecraftServer server);
    }
}
