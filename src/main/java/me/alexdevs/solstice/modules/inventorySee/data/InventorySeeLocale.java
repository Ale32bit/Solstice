package me.alexdevs.solstice.modules.inventorySee.data;

import java.util.Map;

public class InventorySeeLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("exempt", "<gold>You cannot open this inventory because the user is exempt.</gold>"),
            Map.entry("openedInventory", "<gold>Opened <yellow>${user}</yellow>'s inventory.</gold>")
    );
}
