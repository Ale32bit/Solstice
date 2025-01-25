package me.alexdevs.solstice.modules.notifications.data;

public record PlayerNotificationSettings(String soundId, float pitch, float volume, boolean afkOnly) {
}
