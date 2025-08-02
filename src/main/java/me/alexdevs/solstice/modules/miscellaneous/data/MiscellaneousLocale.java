package me.alexdevs.solstice.modules.miscellaneous.data;

import java.util.Map;

public class MiscellaneousLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("noEffects", "<gold>This player has no active effects.</gold>"),
            Map.entry("effectHeader", "<gold>Active effects:</gold>"),
            Map.entry("effect", "<gold><yellow>${effect}</yellow>: <yellow>x${amplifier}</yellow> for <yellow>${duration}</yellow></gold>"),
            Map.entry("infinite", "infinite"),

            Map.entry("top", "<gold>Whoosh!</gold>"),

            Map.entry("walkSpeedReset", "<gold>Walk speed reset.</gold>"),
            Map.entry("walkSpeedSet", "<gold>Walk speed set to <yellow>${speed}</yellow>.</gold>"),
            Map.entry("flySpeedReset", "<gold>Flight speed reset.</gold>"),
            Map.entry("flySpeedSet", "<gold>Flight speed set to <yellow>${speed}</yellow>.</gold>")
    );
}
