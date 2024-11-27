package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.BossBarEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.RestartEvents;
import me.alexdevs.solstice.util.Format;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoRestart {
    private static BossBarManager.TimeBar restartBar = null;
    private static SoundEvent sound;
    private static ScheduledFuture<?> currentSchedule = null;


    public static void register() {
        SolsticeEvents.READY.register((instance, server) -> {
            if (Solstice.config().autoRestart.enableAutoRestart) {
                scheduleNextRestart();
            }
        });

        BossBarEvents.PROGRESS.register((timeBar, server) -> {
            if (restartBar == null || !timeBar.getUuid().equals(restartBar.getUuid()))
                return;

            var notificationTimes = Solstice.config().autoRestart.restartNotifications;

            var remainingSeconds = restartBar.getRemainingSeconds();
            if (notificationTimes.contains(remainingSeconds)) {
                notifyRestart(server, restartBar);
            }

        });

        // Shutdown
        BossBarEvents.END.register((timeBar, server) -> {
            if (restartBar == null || !timeBar.getUuid().equals(restartBar.getUuid()))
                return;

            server.getPlayerManager().getPlayerList().forEach(player -> {
                player.networkHandler.disconnect(Format.parse(Solstice.config().autoRestart.restartKickMessage));
            });
            server.stop(false);
        });

        setup();

        SolsticeEvents.RELOAD.register(instance -> {
            setup();
        });
    }

    private static void setup() {
        var soundName = Solstice.config().autoRestart.restartSound;
        var id = Identifier.tryParse(soundName);
        if (id == null) {
            Solstice.LOGGER.error("Invalid restart notification sound name {}", soundName);
            sound = SoundEvents.BLOCK_NOTE_BLOCK_BELL.value();
        }
        sound = SoundEvent.of(id);
    }

    public static void schedule(int seconds, String message) {
        restartBar = BossBarManager.getInstance().startTimeBar(
                message,
                seconds,
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_20,
                true
        );

        RestartEvents.SCHEDULED.invoker().onSchedule(restartBar);
    }

    public static boolean isScheduled() {
        return restartBar != null || currentSchedule != null && !currentSchedule.isCancelled();
    }

    public static void cancel() {
        if (restartBar != null) {
            BossBarManager.getInstance().cancelTimeBar(restartBar);
            RestartEvents.CANCELED.invoker().onCancel(restartBar);
            restartBar = null;
        }

        if (currentSchedule != null) {
            currentSchedule.cancel(false);
            currentSchedule = null;
        }
    }

    private static void notifyRestart(MinecraftServer server, BossBarManager.TimeBar bar) {
        var solstice = Solstice.getInstance();
        var text = bar.parseLabel(Solstice.config().autoRestart.restartChatMessage);
        solstice.broadcast(text);

        var pitch = Solstice.config().autoRestart.restartSoundPitch;
        server.getPlayerManager().getPlayerList().forEach(player -> {
            player.playSound(sound, SoundCategory.MASTER, 1f, pitch);
        });
    }

    @Nullable
    public static Long scheduleNextRestart() {
        var delay = getNextDelay();
        if (delay == null)
            return null;

        var barTime = 10 * 60;
        // start bar 10 mins earlier
        var barStartTime = delay - barTime;

        currentSchedule = Solstice.scheduler.schedule(() -> {
            schedule(barTime, Solstice.config().autoRestart.restartBarLabel);
        }, barStartTime, TimeUnit.SECONDS);

        Solstice.LOGGER.info("Restart scheduled for in {} seconds", delay);
        return delay;
    }

    @Nullable
    private static Long getNextDelay() {
        var restartTimeStrings = Solstice.config().autoRestart.restartAt;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRunTime = null;
        long shortestDelay = Long.MAX_VALUE;

        for (var timeString : restartTimeStrings) {
            LocalTime targetTime = LocalTime.parse(timeString);
            LocalDateTime targetDateTime = now.with(targetTime);

            if (targetDateTime.isBefore(now)) {
                targetDateTime = targetDateTime.plusDays(1);
            }

            long delay = Duration.between(now, targetDateTime).toSeconds();
            if (delay < shortestDelay) {
                shortestDelay = delay;
                nextRunTime = targetDateTime;
            }
        }

        if (nextRunTime != null) {
            return shortestDelay;
        }
        return null;
    }
}
