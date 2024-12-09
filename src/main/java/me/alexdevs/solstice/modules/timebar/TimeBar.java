package me.alexdevs.solstice.modules.timebar;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;

public class TimeBar {
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
        if (totalSeconds >= 3600) {
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

        var serverContext = PlaceholderContext.of(Solstice.server);

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
