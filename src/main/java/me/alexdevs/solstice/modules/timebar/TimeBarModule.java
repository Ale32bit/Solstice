package me.alexdevs.solstice.modules.timebar;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.TimeBarEvents;
import me.alexdevs.solstice.modules.timebar.commands.TimeBarCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class TimeBarModule {
    private static ConcurrentLinkedDeque<TimeBar> timeBars = new ConcurrentLinkedDeque<>();

    public TimeBarModule() {
        CommandRegistrationCallback.EVENT.register(TimeBarCommand::new);

        Solstice.scheduler.scheduleAtFixedRate(this::updateBars, 0, 1, TimeUnit.SECONDS);
    }

    public void updateBars() {
        for (var timeBar : timeBars) {
            var remove = timeBar.elapse();
            TimeBarEvents.PROGRESS.invoker().onProgress(timeBar, Solstice.server);

            var players = Solstice.server.getPlayerManager().getPlayerList();
            showBar(players, timeBar);

            if (remove) {
                timeBars.remove(timeBar);
                TimeBarEvents.END.invoker().onEnd(timeBar, Solstice.server);
                hideBar(players, timeBar);
            }
        }
    }

    private void showBar(Collection<ServerPlayerEntity> players, TimeBar timeBar) {
        timeBar.getBossBar().addPlayers(players);
    }

    private void hideBar(Collection<ServerPlayerEntity> players, TimeBar timeBar) {
        players.forEach(player -> {
            timeBar.getBossBar().removePlayer(player);

        });
    }

    public TimeBar startTimeBar(String label, int seconds, BossBar.Color color, BossBar.Style style, boolean countdown) {
        var timeBar = new TimeBar(label, seconds, countdown, color, style);

        timeBars.add(timeBar);

        var players = Solstice.server.getPlayerManager().getPlayerList();
        showBar(players, timeBar);

        TimeBarEvents.START.invoker().onStart(timeBar, Solstice.server);
        TimeBarEvents.PROGRESS.invoker().onProgress(timeBar, Solstice.server);

        return timeBar;
    }

    public boolean cancelTimeBar(TimeBar timeBar) {
        var success = timeBars.remove(timeBar);
        if (success) {
            var players = Solstice.server.getPlayerManager().getPlayerList();
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
