package me.alexdevs.solstice.modules.rtp.data;

import java.util.Map;

public class RTPLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("searching", "<yellow>Finding a good spot...</yellow>"),
            Map.entry("timeout", "<gold>Could not find a valid spot in a timely manner.</gold>"),
            Map.entry("tooManyAttempts", "<gold>Too many failed attempts at locating a valid spot.</gold>"),
            Map.entry("unsafe", "<gold>Could not place you in a safe spot.</gold>"),
            Map.entry("success", "<green>Teleporting to a random location...</green>"),
            Map.entry("noWorldPermission", "<gold>You do not have permission to run this command in this world.</gold>"),
            Map.entry("noBiomePermission", "<gold>You do not have permission to use this biome.</gold>")
    );
}
