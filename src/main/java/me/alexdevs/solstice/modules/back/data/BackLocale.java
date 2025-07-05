package me.alexdevs.solstice.modules.back.data;

import java.util.Map;

public class BackLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("teleporting", "<gold>Teleporting to previous location...</gold>"),
            Map.entry("noPosition", "<red>There is no location to return back to.</red>"),
            Map.entry("teleportFailed", "<red>Could not safely teleport to previous location.</red>\n ${forceBackButton}"),
            Map.entry("forceLabel", "<gold>Force back</gold>"),
            Map.entry("forceHover", "Force teleport to previous location. This may be dangerous!")
    );
}
