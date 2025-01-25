package me.alexdevs.solstice.modules.notifications.data;

import java.util.Map;

public class NotificationsLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("setSound", "<gold>Notifications sound set to <yellow>${sound}</yellow>.</gold>"),
            Map.entry("setPitch", "<gold>Notifications pitch set to <yellow>${pitch}</yellow>.</gold>"),
            Map.entry("setVolume", "<gold>Notifications volume set to <yellow>${volume}</yellow>.</gold>"),
            Map.entry("setAfkOnlyEnabled", "<gold>Notifications enabled only while AFK.</gold>"),
            Map.entry("setAfkOnlyDisabled", "<gold>Notifications always enabled.</gold>"),

            Map.entry("toggleEnabled", "<green>Notifications enabled.</green>"),
            Map.entry("toggleDisabled", "<gold>Notifications disabled.</gold>"),

            Map.entry("reset", "<gold>Notification settings cleared.</gold>"),

            Map.entry("getHeader", "<gold>Your notifications settings:</gold>"),
            Map.entry("getEnabled.true", "  <gold>Enabled: <green>enabled</green></gold>"),
            Map.entry("getEnabled.false", "  <gold>Enabled: <red>disabled</red></gold>"),
            Map.entry("getSound", "  <gold>Sound: <yellow>${sound}</yellow></gold>"),
            Map.entry("getPitch", "  <gold>Pitch: <yellow>${pitch}</yellow></gold>"),
            Map.entry("getVolume", "  <gold>Volume: <yellow>${volume}</yellow></gold>"),
            Map.entry("getAfkOnly.true", "  <gold>AFK-Only: <green>enabled</green></gold>"),
            Map.entry("getAfkOnly.false", "  <gold>AFK-Only: <red>disabled</red></gold>")
    );
}
