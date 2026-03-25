package me.alexdevs.solstice.modules.timeBar;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.text.Format;
import net.minecraft.network.chat.Component;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.world.BossEvent;

import java.util.Map;
import java.util.UUID;

public class TimeBar {
    private final UUID uuid = UUID.randomUUID();
    private final CustomBossEvent bossBar;
    private final String label;
    private final int time;
    private final boolean countdown;
    private int elapsedSeconds = 0;

    public TimeBar(String label, int time, boolean countdown, BossEvent.BossBarColor color, BossEvent.BossBarOverlay style) {
        this.bossBar = new CustomBossEvent(SolsticeIdentifier.of(Solstice.MOD_ID, uuid.toString()).get(), Component.nullToEmpty(label));
        this.bossBar.setColor(color);
        this.bossBar.setOverlay(style);
        this.label = label;
        this.time = time;
        this.countdown = countdown;
        updateName();
        updateProgress();
    }

    public void updateName() {
        var text = parseLabel(label);
        bossBar.setName(text);
    }

    public Component parseLabel(String labelString) {
        var totalTime = TimeSpan.toLongString(this.time);
        var elapsedTime = TimeSpan.toLongString(this.elapsedSeconds);

        var remaining = getRemainingSeconds();
        var remainingTime = TimeSpan.toLongString(remaining);

        var placeholders = Map.of(
                "total_time", Component.nullToEmpty(totalTime),
                "elapsed_time", Component.nullToEmpty(elapsedTime),
                "remaining_time", Component.nullToEmpty(remainingTime)
        );

        var serverContext = PlaceholderContext.of(Solstice.server);

        return Format.parse(labelString, serverContext, placeholders);
    }

    public UUID getUuid() {
        return uuid;
    }

    public CustomBossEvent getBossBar() {
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

        bossBar.setProgress(Math.min(
                Math.max(
                        progress,
                        0f),
                1f));
    }
}
