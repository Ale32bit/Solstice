package me.alexdevs.solstice.modules.item.data;

import java.util.Map;

public class ItemLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("noItem", "<gold>You are not holding any item!</gold>"),
            Map.entry("nameSet", "<green>Successfully set the new item name!</green>"),
            Map.entry("nameCleared", "<gold>Item name cleared!</gold>"),
            Map.entry("loreSet", "<green>Successfully set the new item lore!</green>"),
            Map.entry("loreCleared", "<gold>Item lore cleared!</gold>"),
            Map.entry("repaired", "<green>Item repaired!</green>"),
            Map.entry("alreadyRepaired", "<gold>This item is already repaired!</gold>"),
            Map.entry("notRepairable", "<gold>This item is not repairable!</gold>"),
            Map.entry("stackRefilled", "<green>Item stack refilled!</green>")
    );
}
