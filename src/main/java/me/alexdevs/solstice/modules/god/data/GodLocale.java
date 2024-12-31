package me.alexdevs.solstice.modules.god.data;

import java.util.Map;

public class GodLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("enabled", "<green>Invincibility enabled</green>"),
            Map.entry("disabled", "<gold>Invincibility disabled</gold>"),
            Map.entry("enabledForOther", "<green>Invincibility enabled for ${player}</green>"),
            Map.entry("disabledForOther", "<gold>Invincibility disabled for ${player}</gold>")
    );
}
