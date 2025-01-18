package me.alexdevs.solstice.modules.inventorySee.data;

import java.util.Map;

public class InventorySeeLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("exempt", "<gold>You cannot open this inventory because the user is exempt.</gold>"),
            Map.entry("openedInventory", "<gold>Opened <yellow>${user}</yellow>'s inventory.</gold>"),
            Map.entry("openedTrinkets", "<gold>Opened <yellow>${user}</yellow>'s trinkets inventory.</gold>"),
            Map.entry("trinketsNotInstalled", "<gold>Trinkets not available because the mod is missing.</gold>"),
            Map.entry("playerNotFound", "<gold>Player not found!</gold>"),
            Map.entry("offlineNotAllowed", "<gold>You cannot open offline player inventories.</gold>")
    );
}
