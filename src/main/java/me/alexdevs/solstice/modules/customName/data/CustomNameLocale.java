package me.alexdevs.solstice.modules.customName.data;

import java.util.Map;

public class CustomNameLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("setSelf", "<gold>Nickname set to <yellow>${nickname}</yellow>!</gold>"),
            Map.entry("clearedSelf", "<gold>Nickname cleared!</gold>"),
            Map.entry("setOther", "<gold><yellow>${player}</yellow>'s nickname set to <yellow>${nickname}</yellow>!</gold>"),
            Map.entry("clearedOther", "<gold><yellow>${player}</yellow>'s nickname cleared!</gold>"),
            Map.entry("errorEmpty", "<red>Nickname cannot be empty!</red>"),
            Map.entry("errorInvalid", "<red>This nickname is invalid!</red>")
    );
}
