package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.BossBarEvents;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class BossBarManager {
    private static BossBarManager instance;

    public static BossBarManager getInstance() {
        return instance;
    }

    private static MinecraftServer server;
    private static ConcurrentLinkedDeque<TimeBar> timeBars = new ConcurrentLinkedDeque<>();

    public static void register() {
        instance = new BossBarManager();

        ServerLifecycleEvents.SERVER_STARTING.register(s -> server = s);

        Solstice.scheduler.scheduleAtFixedRate(BossBarManager::updateBars, 0, 1, TimeUnit.SECONDS);
    }

    public static void updateBars() {
        for (var timeBar : timeBars) {
            var remove = timeBar.elapse();
            BossBarEvents.PROGRESS.invoker().onProgress(timeBar, server);

            var players = server.getPlayerManager().getPlayerList();
            showBar(players, timeBar);

            if (remove) {
                timeBars.remove(timeBar);
                BossBarEvents.END.invoker().onEnd(timeBar, server);
                hideBar(players, timeBar);
            }
        }
    }

    private static void showBar(Collection<ServerPlayerEntity> players, TimeBar timeBar) {
        timeBar.getBossBar().addPlayers(players);
    }

    private static void hideBar(Collection<ServerPlayerEntity> players, TimeBar timeBar) {
        players.forEach(player -> {
            timeBar.getBossBar().removePlayer(player);

        });
    }

    public TimeBar startTimeBar(String label, int seconds, BossBar.Color color, BossBar.Style style, boolean countdown) {
        var timeBar = new TimeBar(label, seconds, countdown, color, style);

        timeBars.add(timeBar);

        var players = server.getPlayerManager().getPlayerList();
        showBar(players, timeBar);

        BossBarEvents.START.invoker().onStart(timeBar, server);
        BossBarEvents.PROGRESS.invoker().onProgress(timeBar, server);

        return timeBar;
    }

    public boolean cancelTimeBar(TimeBar timeBar) {
        var success = timeBars.remove(timeBar);
        if (success) {
            var players = server.getPlayerManager().getPlayerList();
            hideBar(players, timeBar);
            BossBarEvents.CANCEL.invoker().onCancel(timeBar, server);
        }
        return success;
    }

    public boolean cancelTimeBar(UUID uuid) {
        var progressBar = timeBars.stream().filter(p -> p.uuid.equals(uuid)).findFirst().orElse(null);
        if (progressBar == null) {
            return false;
        }

        return cancelTimeBar(progressBar);
    }

    public static class TimeBar {
        private final UUID uuid = UUID.randomUUID();
        private final CommandBossBar bossBar;
        private final String label;
        private final int time;
        private int elapsedSeconds = 0;
        private final boolean countdown;

        public TimeBar(String label, int time, boolean countdown, BossBar.Color color, BossBar.Style style) {
            this.bossBar = new CommandBossBar(Identifier.of(Solstice.MOD_ID, uuid.toString()), Text.of(label));
            this.bossBar.setColor(color);
            this.bossBar.setStyle(style);
            this.label = label;
            this.time = time;
            this.countdown = countdown;
            updateName();
            updateProgress();
        }

        public static String formatTime(int totalSeconds) {
            var hours = totalSeconds / 3600;
            var minutes = (totalSeconds / 60) % 60;
            var seconds = totalSeconds % 60;
            if(totalSeconds >= 3600) {
                return String.format("%dh%dm%ds", hours, minutes, seconds);
            }
            return String.format("%dm%ds", minutes, seconds);
        }

        public void updateName() {
            var text = parseLabel(label);
            bossBar.setName(text);
        }

        public Text parseLabel(String labelString) {
            var totalTime = formatTime(this.time);
            var elapsedTime = formatTime(this.elapsedSeconds);

            var remaining = getRemainingSeconds();
            var remainingTime = formatTime(remaining);

            var placeholders = Map.of(
                    "total_time", Text.of(totalTime),
                    "elapsed_time", Text.of(elapsedTime),
                    "remaining_time", Text.of(remainingTime)
            );

            var serverContext = PlaceholderContext.of(server);

            return Format.parse(labelString, serverContext, placeholders);
        }

        public UUID getUuid() {
            return uuid;
        }

        public CommandBossBar getBossBar() {
            return bossBar;
        }

        public String getLabel() {
            return label;
        }

        public int getTime() {
            return time;
        }

        public int getElapsedSeconds() {
            return elapsedSeconds;
        }

        public int getRemainingSeconds() {
            return time - elapsedSeconds;
        }

        public boolean isCountdown() {
            return countdown;
        }

        public boolean elapse() {
            this.elapsedSeconds++;

            updateProgress();
            updateName();

            return this.elapsedSeconds >= this.time;
        }

        private void updateProgress() {
            float progress = (float) elapsedSeconds / (float) time;
            if (countdown) {
                progress = 1f - progress;
            }

            bossBar.setPercent(Math.min(
                    Math.max(
                            progress,
                            0f),
                    1f));
        }
    }
}
