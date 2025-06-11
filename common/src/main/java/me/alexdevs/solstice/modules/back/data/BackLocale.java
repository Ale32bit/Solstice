package me.alexdevs.solstice.modules.back.data;

import java.util.Map;

public class BackLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("teleporting", "<gold>Teleporting to previous position...</gold>"),
            Map.entry("noPosition", "<red>There is no position to return back to.</red>")
    );
}
