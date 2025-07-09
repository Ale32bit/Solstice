package me.alexdevs.solstice.modules.heal.data;

import java.util.Map;

public class HealLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("healed", "<gold>Healed <yellow>${entity}</yellow>!</gold>"),
            Map.entry("healedMultiple", "<gold>Healed <yellow>${count}</yellow> entities!</gold>"),
            Map.entry("noLiving", "<red>There are no living entities in the selector!</red>")
    );
}
