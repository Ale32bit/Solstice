package me.alexdevs.solstice.modules.enderchest.data;

import java.util.Map;

public class EnderChestLocale {
    public static final Map<String, String> LOCALE = Map.ofEntries(
            Map.entry("title", "${player}'s Ender Chest"),
            Map.entry("exempt", "<red>You cannot open this Ender Chest because the player is exempt.</red>"),
            Map.entry("opened", "<gold>Opened <yellow>${player}</yellow>'s Ender Chest.</gold>")
    );
}
