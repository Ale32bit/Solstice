package me.alexdevs.solstice.modules.fly.data;

import java.util.Map;

public class FlyLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("enabled", "<green>Flight enabled</green>"),
            Map.entry("disabled", "<gold>Flight disabled</gold>"),
            Map.entry("enabledForOther", "<green>Flight enabled for ${player}</green>"),
            Map.entry("disabledForOther", "<gold>Flight disabled for ${player}</gold>")
    );
}
