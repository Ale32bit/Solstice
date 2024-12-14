package me.alexdevs.solstice.modules.helpop.data;

import java.util.Map;

public class HelpOpLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("helpRequestMessage", "❗ <gold>[%player:displayname%]</gold> <gray>${message}</gray>"),
            Map.entry("helpRequestFeedback", "<gold>Help request sent: </gold><gray>${message}</gray>")
    );
}
