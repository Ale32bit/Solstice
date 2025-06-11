package me.alexdevs.solstice.modules.timeBar;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.TimeBarEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.timeBar.commands.TimeBarCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class TimeBarModule extends ModuleBase.Toggleable {
    public static final String ID = "timebar";
    private static final ConcurrentLinkedDeque<TimeBar> timeBars = new ConcurrentLinkedDeque<>();

    public TimeBarModule() {
        super(ID);
    }

    @Override
    public void init() {
        commands.add(new TimeBarCommand(this));

        Solstice.scheduler.scheduleAtFixedRate(this::updateBars, 0, 1, TimeUnit.SECONDS);
    }

    public void updateBars() {
        for (var timeBar : timeBars) {
            var remove = timeBar.elapse();
            TimeBarEvents.PROGRESS.invoker().onProgress(timeBar, Solstice.server);

            var players = Solstice.server.getPlayerList().getPlayers();
            showBar(players, timeBar);

            if (remove) {
                timeBars.remove(timeBar);
                TimeBarEvents.END.invoker().onEnd(timeBar, Solstice.server);
                hideBar(players, timeBar);
            }
        }
    }

    private void showBar(Collection<ServerPlayer> players, TimeBar timeBar) {
        try {
            timeBar.getBossBar().setPlayers(players);
        } catch (Exception e) {
            Solstice.LOGGER.error("Error while showing boss bar to players", e);
        }
    }

    private void hideBar(Collection<ServerPlayer> players, TimeBar timeBar) {
        players.forEach(player -> {
            try {
                timeBar.getBossBar().removePlayer(player);
            } catch (Exception e) {
                Solstice.LOGGER.error("Error while hiding boss bar from players", e);
            }
        });
    }

    public TimeBar startTimeBar(String label, int seconds, BossEvent.BossBarColor color, BossEvent.BossBarOverlay style, boolean countdown) {
        var timeBar = new TimeBar(label, seconds, countdown, color, style);

        Solstice.scheduler.schedule(() -> {
            timeBars.add(timeBar);

            var players = Solstice.server.getPlayerList().getPlayers();
            showBar(players, timeBar);

            TimeBarEvents.START.invoker().onStart(timeBar, Solstice.server);
            TimeBarEvents.PROGRESS.invoker().onProgress(timeBar, Solstice.server);
        }, 0, TimeUnit.SECONDS);

        return timeBar;
    }

    public boolean cancelTimeBar(TimeBar timeBar) {
        var success = timeBars.remove(timeBar);
        if (success) {
            var players = Solstice.server.getPlayerList().getPlayers();
            hideBar(players, timeBar);
            TimeBarEvents.CANCEL.invoker().onCancel(timeBar, Solstice.server);
        }
        return success;
    }

    public boolean cancelTimeBar(UUID uuid) {
        var progressBar = timeBars.stream().filter(p -> p.getUuid().equals(uuid)).findFirst().orElse(null);
        if (progressBar == null) {
            return false;
        }

        return cancelTimeBar(progressBar);
    }
}
