package me.alexdevs.solstice.modules.autorestart;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.TimeBarEvents;
import me.alexdevs.solstice.api.events.RestartEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.autorestart.commands.RestartCommand;
import me.alexdevs.solstice.modules.autorestart.data.AutoRestartConfig;
import me.alexdevs.solstice.modules.autorestart.data.AutoRestartLocale;
import me.alexdevs.solstice.modules.timebar.TimeBar;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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

public class AutoRestartModule {
    public static final String ID = "autorestart";

    private static TimeBar restartBar = null;
    private static SoundEvent sound;
    private static ScheduledFuture<?> currentSchedule = null;

    private static AutoRestartConfig config;
    private static Locale locale;

    public AutoRestartModule() {
        Solstice.configManager.registerData(ID, AutoRestartConfig.class, AutoRestartConfig::new);
        Solstice.localeManager.registerModule(ID, AutoRestartLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(RestartCommand::new);

        SolsticeEvents.READY.register((instance, server) -> {
            config = Solstice.configManager.getData(AutoRestartConfig.class);
            locale = Solstice.localeManager.getLocale(ID);
            setup();
            if (config.enable) {
                scheduleNextRestart();
            }
        });

        TimeBarEvents.PROGRESS.register((timeBar, server) -> {
            if (restartBar == null || !timeBar.getUuid().equals(restartBar.getUuid()))
                return;

            var notificationTimes = config.restartNotifications;

            var remainingSeconds = restartBar.getRemainingSeconds();
            if (notificationTimes.contains(remainingSeconds)) {
                notifyRestart(server, restartBar);
            }

        });

        // Shutdown
        TimeBarEvents.END.register((timeBar, server) -> {
            if (restartBar == null || !timeBar.getUuid().equals(restartBar.getUuid()))
                return;

            server.getPlayerManager().getPlayerList().forEach(player -> {
                player.networkHandler.disconnect(locale.get("kickMessage"));
            });
            server.stop(false);
        });

        SolsticeEvents.RELOAD.register(instance -> {
            setup();
        });
    }

    private void setup() {
        var soundName = config.restartSound;
        var id = Identifier.tryParse(soundName);
        if (id == null) {
            Solstice.LOGGER.error("Invalid restart notification sound name {}", soundName);
            sound = SoundEvents.BLOCK_NOTE_BLOCK_BELL.value();
        }
        sound = SoundEvent.of(id);
    }

    public void schedule(int seconds, String message) {
        restartBar = Solstice.modules.timeBar.startTimeBar(
                message,
                seconds,
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_20,
                true
        );

        RestartEvents.SCHEDULED.invoker().onSchedule(restartBar);
    }

    public boolean isScheduled() {
        return restartBar != null || currentSchedule != null && !currentSchedule.isCancelled();
    }

    public void cancel() {
        if (restartBar != null) {
            Solstice.modules.timeBar.cancelTimeBar(restartBar);
            RestartEvents.CANCELED.invoker().onCancel(restartBar);
            restartBar = null;
        }

        if (currentSchedule != null) {
            currentSchedule.cancel(false);
            currentSchedule = null;
        }
    }

    private void notifyRestart(MinecraftServer server, TimeBar bar) {
        var solstice = Solstice.getInstance();
        var text = bar.parseLabel(locale.raw("chatMessage"));
        solstice.broadcast(text);

        var pitch = config.restartSoundPitch;
        server.getPlayerManager().getPlayerList().forEach(player -> {
            player.playSound(sound, SoundCategory.MASTER, 1f, pitch);
        });
    }

    @Nullable
    public Long scheduleNextRestart() {
        var delay = getNextDelay();
        if (delay == null)
            return null;

        var barTime = 10 * 60;
        // start bar 10 mins earlier
        var barStartTime = delay - barTime;

        currentSchedule = Solstice.scheduler.schedule(() -> {
            schedule(barTime, locale.raw("barLabel"));
        }, barStartTime, TimeUnit.SECONDS);

        Solstice.LOGGER.info("Restart scheduled for in {} seconds", delay);
        return delay;
    }

    @Nullable
    private Long getNextDelay() {
        var restartTimeStrings = config.restartAt;
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
