package me.alexdevs.solstice.modules.jail.data;

import java.util.Map;

public class JailLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("cannotRunCommands", "<red>You are not allowed to run this command in jail!</red>"),
            Map.entry("cannotBreakBlocks", "<red>You are not allowed to break blocks in jail!</red>"),
            Map.entry("cannotAttackEntities", "<red>You are not allowed to attack entities in jail!</red>"),
            Map.entry("cannotUseBlocks", "<red>You are not allowed to use blocks in jail!</red>"),
            Map.entry("cannotUseEntities", "<red>You are not allowed to interact with entities in jail!</red>"),
            Map.entry("cannotUseItems", "<red>You are not allowed to use items in jail!</red>")
    );
}
