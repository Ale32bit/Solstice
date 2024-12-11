package me.alexdevs.solstice.modules.fun.data;

import java.util.Map;

public class FunLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("hatEmptyStack", "<red>You are not holding any item!</red>"),
            Map.entry("hatSuccess", "<gold>Check out your new hat!</gold>")
    );
}
