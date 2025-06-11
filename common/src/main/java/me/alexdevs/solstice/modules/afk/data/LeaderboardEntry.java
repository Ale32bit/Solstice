package me.alexdevs.solstice.modules.afk.data;

import java.util.UUID;

public class LeaderboardEntry {
    protected String name;
    protected final UUID uuid;
    protected int activeTime;

    public LeaderboardEntry(String name, UUID uuid, int activeTime) {
        this.name = name;
        this.uuid = uuid;
        this.activeTime = activeTime;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public UUID uuid() {
        return uuid;
    }

    public int activeTime() {
        return activeTime;
    }

    public void activeTime(int activeTime) {
        this.activeTime = activeTime;
    }
}
