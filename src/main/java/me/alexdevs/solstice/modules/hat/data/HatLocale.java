package me.alexdevs.solstice.modules.hat.data;

import java.util.Map;

public class HatLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("emptyStack", "<red>You are not holding any item!</red>"),
            Map.entry("success", "<green>Check out your new hat!</green>"),
            Map.entry("notAllowed", "<gold>You cannot wear this item!</gold>")
    );
}
