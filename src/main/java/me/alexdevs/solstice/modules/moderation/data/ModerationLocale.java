package me.alexdevs.solstice.modules.moderation.data;

import java.util.Map;

public class ModerationLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("youAreMuted", "<gold>You are muted!</gold>"),
            Map.entry("playerNotFound", "<red>Could not find this player</red>"),
            Map.entry("targetIsSelf", "<red>You cannot ignore yourself.</red>"),
            Map.entry("blockedPlayer", "<yellow>${targetName}</yellow> <gold>is now ignored.</gold>"),
            Map.entry("unblockedPlayer", "<yellow>${targetName}</yellow> <green>is no longer ignored.</green>"),
            Map.entry("ignoreList", "<gold>Ignored players: ${playerList}</gold>"),
            Map.entry("ignoreListFormat", "<run_cmd:'/ignore ${player}'><hover:'Click to unblock'><yellow>${player}</yellow></hover></run_cmd>"),
            Map.entry("ignoreListComma", "<gold>, </gold>"),
            Map.entry("ignoreListEmpty ", "<gold>You are not ignoring anyone at the moment.</gold>"),
            Map.entry("banMessageFormat", "<red>You are banned from this server:</red>\n\n${reason}"),
            Map.entry("tempBanMessageFormat", "<red>You are temporarily banned from this server:</red>\n\n${reason}\n\n<gray>Expires: ${expiry_date}</gray>")
    );
}
