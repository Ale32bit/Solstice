package me.alexdevs.solstice.modules.restart;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.RestartEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.TimeBarEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.restart.commands.RestartCommand;
import me.alexdevs.solstice.modules.restart.data.RestartConfig;
import me.alexdevs.solstice.modules.restart.data.RestartLocale;
import me.alexdevs.solstice.modules.timeBar.TimeBar;
import me.alexdevs.solstice.modules.timeBar.TimeBarModule;
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

public class RestartModule extends ModuleBase.Toggleable {
    public static final String ID = "restart";

    private static final BossBar.Color fallbackBarColor = BossBar.Color.RED;
    private static final BossBar.Style fallbackBarStyle = BossBar.Style.NOTCHED_10;

    private TimeBar restartBar = null;
    private SoundEvent sound;
    private ScheduledFuture<?> currentSchedule = null;

    public RestartModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, RestartConfig.class, RestartConfig::new);
        Solstice.localeManager.registerModule(ID, RestartLocale.MODULE);

        commands.add(new RestartCommand(this));

        SolsticeEvents.READY.register((instance, server) -> {
            setup();
            if (getConfig().enable) {
                scheduleNextRestart();
            }
        });

        TimeBarEvents.PROGRESS.register((timeBar, server) -> {
            if (restartBar == null || !timeBar.getUuid().equals(restartBar.getUuid()))
                return;

            var notificationTimes = getConfig().restartNotifications;

            var remainingSeconds = restartBar.getRemainingSeconds();
            if (notificationTimes.contains(remainingSeconds)) {
                notifyRestart(server, restartBar);
            }

        });

        // Shutdown
        TimeBarEvents.END.register((timeBar, server) -> {
            if (restartBar == null || !timeBar.getUuid().equals(restartBar.getUuid()))
                return;

            restart();
        });

        SolsticeEvents.RELOAD.register(instance -> setup());
    }

    @Override
    public boolean isEnabled() {
        if(!Solstice.modules.getModule(TimeBarModule.class).isEnabled())
            return false;

        return super.isEnabled();
    }

    public RestartConfig getConfig() {
        return Solstice.configManager.getData(RestartConfig.class);
    }

    public BossBar.Style getBarStyle() {
        var styleName = getConfig().barStyle;
        try {
            return BossBar.Style.valueOf(styleName);
        } catch (IllegalArgumentException e) {
            Solstice.LOGGER.error("Invalid value in `restart -> bar-style` setting.");
            return fallbackBarStyle;
        }
    }

    public BossBar.Color getBarColor() {
        var colorName = getConfig().barColor;
        try {
            return BossBar.Color.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            Solstice.LOGGER.error("Invalid value in `restart -> bar-color` setting.");
            return fallbackBarColor;
        }
    }

    public void restart() {
        Solstice.server.getPlayerManager().getPlayerList().forEach(player -> player.networkHandler.disconnect(locale().get("kickMessage")));

        Solstice.nextTick(() -> Solstice.server.stop(false));
    }

    private void setup() {
        var soundName = getConfig().restartSound;
        var id = Identifier.tryParse(soundName);
        if (id == null) {
            Solstice.LOGGER.error("Invalid restart notification sound name {}", soundName);
            sound = SoundEvents.BLOCK_NOTE_BLOCK_BELL.value();
        } else {
            sound = SoundEvent.of(id);
        }
    }

    public void schedule(int seconds, String message) {
        if(isRunning()) {
            Solstice.LOGGER.warn("Could not start a new restart countdown because there is one already running.");
            return;
        }

        var timeBar = Solstice.modules.getModule(TimeBarModule.class);
        restartBar = timeBar.startTimeBar(
                message,
                seconds,
                getBarColor(),
                getBarStyle(),
                true
        );

        RestartEvents.SCHEDULED.invoker().onSchedule(restartBar);
    }

    public boolean isScheduled() {
        return restartBar != null || currentSchedule != null && !currentSchedule.isCancelled();
    }

    public boolean isRunning() {
        return restartBar != null;
    }

    public void cancel() {
        var timeBar = Solstice.modules.getModule(TimeBarModule.class);
        if (restartBar != null) {
            timeBar.cancelTimeBar(restartBar);
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
        var text = bar.parseLabel(locale().raw("chatMessage"));
        solstice.broadcast(text);

        var pitch = getConfig().restartSoundPitch;
        server.getPlayerManager().getPlayerList().forEach(player -> player.playSound(sound, SoundCategory.MASTER, 1f, pitch));
    }

    @Nullable
    public Long scheduleNextRestart() {
        var delay = getNextDelay();
        if (delay == null)
            return null;

        var barTime = getConfig().restartNotifications.stream().max(Integer::compareTo).orElse(600);
        var barStartTime = delay - barTime;

        currentSchedule = Solstice.scheduler.schedule(() -> schedule(barTime, locale().raw("barLabel")), barStartTime, TimeUnit.SECONDS);

        Solstice.LOGGER.info("Restart scheduled for in {} seconds", delay);
        return delay;
    }

    @Nullable
    private Long getNextDelay() {
        var restartTimeStrings = getConfig().restartAt;
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
