package me.alexdevs.solstice.modules.jail.data;

import java.util.Map;

public class JailLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("jailNotFound", "<gold>This jail does not exist.</gold>"),
            Map.entry("jailAlreadyExists", "<gold>A jail with this name already exists.</gold>"),
            Map.entry("teleporting", "<gold>Teleporting to <yellow>${jail}</yellow>...</gold>"),
            Map.entry("created", "<green>New jail <yellow>${jail}</yellow> created!</green>"),
            Map.entry("deleted", "<gold>Jail <yellow>${jail}</yellow> deleted!</gold>"),
            Map.entry("notJailed", "<gold>This user is not jailed!</gold>"),
            Map.entry("alreadyJailed", "<gold>This player is already jailed!</gold>"),
            Map.entry("jailed", "<gold><yellow>${player}</yellow> has been jailed at <yellow>${jail}</yellow> indefinitely!</gold>"),
            Map.entry("jailedFor", "<gold><yellow>${player}</yellow> has been jailed at <yellow>${jail}</yellow> for <yellow>${duration}</yellow>!</gold>"),
            Map.entry("jailedForWithReason", "<gold><yellow>${player}</yellow> has been jailed at <yellow>${jail}</yellow> for <yellow>${duration}</yellow> with reason: <yellow>${reason}</yellow></gold>"),
            Map.entry("playerJailed", "<red>You have been jailed!</red>"),
            Map.entry("playerJailedFor", "<red>You have been jailed for <yellow>${duration}</yellow>!</red>"),
            Map.entry("playerJailedForWithReason", "<red>You have been jailed for <yellow>${duration}</yellow> with reason: <yellow>${reason}</yellow></red>"),
            Map.entry("playerUnjailed", "<green>You have been unjailed!</green>"),
            Map.entry("unjailed", "<gold>Unjailed <yellow>${player}</yellow>!</gold>"),
            Map.entry("cannotRunCommands", "<red>You are not allowed to run this command in jail!</red>"),
            Map.entry("cannotBreakBlocks", "<red>You are not allowed to break blocks in jail!</red>"),
            Map.entry("cannotAttackEntities", "<red>You are not allowed to attack entities in jail!</red>"),
            Map.entry("cannotUseBlocks", "<red>You are not allowed to use blocks in jail!</red>"),
            Map.entry("cannotUseEntities", "<red>You are not allowed to interact with entities in jail!</red>"),
            Map.entry("cannotUseItems", "<red>You are not allowed to use items in jail!</red>"),
            Map.entry("playerExempt", "<gold>This player is exempt from being jailed!</gold>"),
            Map.entry("jailList", "<gold>Jails: ${list}.</gold>"),
            Map.entry("listEntry", "<yellow>${jail}</yellow>"),
            Map.entry("comma", "<gold>, </gold>")
    );
}
