package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.core.BossBarManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class RestartEvents {
    public static final Event<Schedule> SCHEDULED = EventFactory.createArrayBacked(Schedule.class, callbacks ->
            (timeBar) -> {
                for (Schedule callback : callbacks) {
                    callback.onSchedule(timeBar);
                }
            });

    public static final Event<Cancel> CANCELED = EventFactory.createArrayBacked(Cancel.class, callbacks ->
            (timeBar) -> {
                for (Cancel callback : callbacks) {
                    callback.onCancel(timeBar);
                }
            });

    @FunctionalInterface
    public interface Schedule {
        void onSchedule(BossBarManager.TimeBar timeBar);
    }

    @FunctionalInterface
    public interface Cancel {
        void onCancel(BossBarManager.TimeBar timeBar);
    }
}
