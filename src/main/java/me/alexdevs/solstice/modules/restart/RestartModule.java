package me.alexdevs.solstice.modules.restart;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.RestartEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.events.TimeBarEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.integrations.ConnectorIntegration;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.restart.commands.RestartCommand;
import me.alexdevs.solstice.modules.restart.data.RestartConfig;
import me.alexdevs.solstice.modules.restart.data.RestartLocale;
import me.alexdevs.solstice.modules.timeBar.TimeBar;
import me.alexdevs.solstice.modules.timeBar.TimeBarModule;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
public class RestartModule extends ModuleBase.Toggleable {
    

    private static final BossEvent.BossBarColor fallbackBarColor = BossEvent.BossBarColor.RED;
    private static final BossEvent.BossBarOverlay fallbackBarStyle = BossEvent.BossBarOverlay.NOTCHED_10;

    private TimeBar restartBar = null;
    private SoundEvent sound;
    private ScheduledFuture<?> currentSchedule = null;

    public RestartModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(RestartConfig.class, RestartConfig::new);
        registerLocale(RestartLocale.MODULE);

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
        if(!ModuleProvider.TIMEBAR.isEnabled())
            return false;
        
        return super.isEnabled();
    }

    public RestartConfig getConfig() {
        return Solstice.configManager.getData(RestartConfig.class);
    }

    public BossEvent.BossBarOverlay getBarStyle() {
        var styleName = getConfig().barStyle;
        try {
            return BossEvent.BossBarOverlay.valueOf(styleName);
        } catch (IllegalArgumentException e) {
            Solstice.LOGGER.error("Invalid value in `restart -> bar-style` setting.");
            return fallbackBarStyle;
        }
    }

    public BossEvent.BossBarColor getBarColor() {
        var colorName = getConfig().barColor;
        try {
            return BossEvent.BossBarColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            Solstice.LOGGER.error("Invalid value in `restart -> bar-color` setting.");
            return fallbackBarColor;
        }
    }

    public void restart() {
        if (!ConnectorIntegration.isForge()) {
            var kickMessage = locale().get("kickMessage");
            for (var player : Solstice.server.getPlayerList().getPlayers()) {
                try {
                    player.connection.disconnect(kickMessage);
                } catch (Exception e) {
                    Solstice.LOGGER.error("An error occurred while disconnecting player {}", player, e);
                }
            }
        } else {
            Solstice.LOGGER.warn("Kicking players with restart message is not compatible with Forge at the moment.");
        }

        Solstice.server.halt(false);
    }

    private void setup() {
        var soundName = getConfig().restartSound;
        var id = SolsticeIdentifier.tryParse(soundName);
        if (id == null) {
            Solstice.LOGGER.error("Invalid restart notification sound name {}", soundName);
            sound = SoundEvents.NOTE_BLOCK_BELL.value();
        } else {
            sound = SoundEvent.createVariableRangeEvent(id.get());
        }
    }

    public void schedule(int seconds, String message, RestartEvents.RestartType restartType) {
        if (isRunning()) {
            Solstice.LOGGER.warn("Could not start a new restart countdown because there is one already running.");
            return;
        }

        var timeBar = Solstice.modules.getModule(TimeBarModule.class);
        restartBar = ModuleProvider.TIMEBAR.startTimeBar(
                message,
                seconds,
                getBarColor(),
                getBarStyle(),
                true
        );

        RestartEvents.SCHEDULED.invoker().onSchedule(restartBar, restartType);
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
            ModuleProvider.TIMEBAR.cancelTimeBar(restartBar);
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
        server.getPlayerList().getPlayers().forEach(player -> PlayerUtils.playSound(player,sound, 1f, pitch));
    }

    @Nullable
    public Long scheduleNextRestart() {
        var delay = getNextDelay();
        if (delay == null)
            return null;

        var barTime = getConfig().restartNotifications.stream().max(Integer::compareTo).orElse(600);
        var barStartTime = delay - barTime;

        currentSchedule = Solstice.scheduler.schedule(() -> schedule(barTime, locale().raw("barLabel"), RestartEvents.RestartType.AUTOMATIC), barStartTime, TimeUnit.SECONDS);

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
