package me.alexdevs.solstice.modules.cooldown.data;

import java.util.Map;

public class CooldownLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("cooldown", "<gold>You are on cooldown for <yellow>${timespan}</yellow>.</gold>"),
            Map.entry("checkResult", "<gold><yellow>${player}</yellow> has a cooldown of <yellow>${timespan}</yellow> for <yellow>${key}</yellow>.</gold>"),
            Map.entry("noCooldown", "<gold><yellow>${player}</yellow> has no cooldown for <yellow>${key}</yellow>.</gold>"),
            Map.entry("exempt", "<gold><yellow>${player}</yellow> is exempt for <yellow>${key}</yellow>.</gold>"),
            Map.entry("cleared", "<gold>Cleared <yellow>${key}</yellow> cooldown for <yellow>${player}</yellow>.</gold>")
    );
}
