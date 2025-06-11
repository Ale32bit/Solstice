package me.alexdevs.solstice.api.events;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import me.alexdevs.solstice.modules.timeBar.TimeBar;

public class RestartEvents {
    public static final Event<Schedule> SCHEDULED = EventFactory.createArrayBacked(Schedule.class, callbacks ->
            (timeBar, type) -> {
                for (Schedule callback : callbacks) {
                    callback.onSchedule(timeBar, type);
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
        void onSchedule(TimeBar timeBar, RestartType type);
    }

    @FunctionalInterface
    public interface Cancel {
        void onCancel(TimeBar timeBar);
    }

    public enum RestartType {
        AUTOMATIC,
        MANUAL
    }
}
