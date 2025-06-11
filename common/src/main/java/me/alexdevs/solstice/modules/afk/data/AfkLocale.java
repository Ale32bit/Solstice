package me.alexdevs.solstice.modules.afk.data;

import java.util.Map;

public class AfkLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("goneAfk", "<gray>%player:displayname% is now AFK</gray>"),
            Map.entry("returnAfk", "<gray>%player:displayname% is no longer AFK</gray>"),
            Map.entry("yourActiveTime", "<gold>Your active time is <yellow>${activeTime}</yellow>.</gold>"),
            Map.entry("playerActiveTime", "<gold><yellow>${player}</yellow>'s active time is <yellow>${activeTime}</yellow>.</gold>"),
            Map.entry("neverPlayed", "<gold>This player never played.</gold>"),
            Map.entry("notFound", "<gold>Player not found.</gold>"),
            Map.entry("leaderboardHeader", "<gold>Active Time Leaderboard:</gold>"),
            Map.entry("leaderboardEntry", "<gold> <gold>${index}.</gold> <yellow>${player}</yellow>: <gray>${time}</gray></gold>"),
            Map.entry("activeTimeSet", "<gold>Set <yellow>${player}</yellow>'s active time to <yellow>${time}</yellow>!</gold>"),
            Map.entry("leaderboardRecalculated", "<gold>Recalculated active time leaderboard!</gold>")
    );
}
