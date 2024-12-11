package me.alexdevs.solstice.modules.afk.data;

import java.util.Map;

public class AfkLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("goneAfk", "<gray>%player:displayname% is now AFK</gray>"),
            Map.entry("returnAfk", "<gray>%player:displayname% is no longer AFK</gray>")
    );
}
