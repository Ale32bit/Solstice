package me.alexdevs.solstice.modules.powertool.data;

import java.util.Map;

public class PowerToolLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("emptyHand", "<gold>You are not holding any item.</gold>"),
            Map.entry("actionSet", "<green>Command bound to <yellow>${item}</yellow> on <yellow>${action}</yellow>.</green>"),
            Map.entry("actionCleared", "<gold>Command cleared from <yellow>${item}</yellow> on <yellow>${action}</yellow>.</gold>"),
            Map.entry("allCleared", "<gold>All commands cleared from <yellow>${item}</yellow>!</gold>"),
            Map.entry("noAction", "<gold>This action is not bound for this item.</gold>"),
            Map.entry("check", "<gold>Actions for <yellow>${item}</yellow>:</gold>"),
            Map.entry("checkEntry", "<gold> - <yellow>${action}</yellow>: <gray>${command}</gray></gold>"),
            Map.entry("checkEntryNotSet", "<gold> - <yellow>${action}</yellow>: <gray><i>Not set</i></gray></gold>")
    );
}
