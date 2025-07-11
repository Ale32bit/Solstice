package me.alexdevs.solstice.modules.mute.data;

import java.util.Map;

public class MuteLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("muted", "<gold>Muted <yellow>${player}</yellow>!</gold>"),
            Map.entry("mutedMultiple", "<gold>Muted <yellow>${count}</yellow> players!</gold>"),
            Map.entry("mutedTimespan", "<gold>Muted <yellow>${player}</yellow> for <yellow>${timespan}</yellow>!</gold>"),
            Map.entry("mutedMultipleTimespan", "<gold>Muted <yellow>${count}</yellow> players for <yellow>${timespan}</yellow>!</gold>"),

            Map.entry("unmuted", "<gold>Unmuted <yellow>${player}</yellow>!</gold>"),
            Map.entry("unmutedMultiple", "<gold>Unmuted <yellow>${count}</yellow> players!</gold>"),

            Map.entry("youAreMuted", "<gold>You are muted!</gold>")
    );
}
