package me.alexdevs.solstice.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler extends ScheduledThreadPoolExecutor {
    private final ConcurrentLinkedQueue<Runnable> queue;

    public Scheduler(int corePoolSize, ConcurrentLinkedQueue<Runnable> queue) {
        super(corePoolSize);
        this.queue = queue;
    }

    public ScheduledFuture<?> scheduleSync(Runnable command, long delay, TimeUnit unit) {
        return this.schedule(() -> queue.add(command), delay, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelaySync(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.scheduleWithFixedDelay(() -> queue.add(command), initialDelay, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRateSync(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.scheduleAtFixedRate(() -> queue.add(command), initialDelay, period, unit);
    }
}
