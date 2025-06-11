package me.alexdevs.solstice.modules.near.data;

import java.util.Map;

public class NearLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("noOne", "<gold>There are no players near you.</gold>"),
            Map.entry("nearestPlayers", "<gold>Nearest players: ${playerList}</gold>"),
            Map.entry("format", "${player} <gold>(</gold><yellow>${distance}</yellow><gold>)</gold>"),
            Map.entry("comma", "<gold>, </gold>")
    );
}
