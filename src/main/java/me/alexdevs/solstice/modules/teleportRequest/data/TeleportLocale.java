package me.alexdevs.solstice.modules.teleportRequest.data;

import java.util.Map;

public class TeleportLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("teleporting", "<gold>Teleporting...</gold>"),
            Map.entry("requestSent", "<gold>Teleport request sent.</gold>"),
            Map.entry("requestSentAll", "<gold>Teleport request sent to all players online.</gold>"),
            Map.entry("pendingTeleport", "${requesterPlayer} <gold>requested to teleport to you.</gold>\n ${acceptButton} ${refuseButton}"),
            Map.entry("pendingTeleportHere", "${requesterPlayer} <gold>requested you to teleport to them.</gold>\n ${acceptButton} ${refuseButton}"),
            Map.entry("noPending", "<gold>There are no pending teleport requests for you.</gold>"),
            Map.entry("unavailable", "<red>This request expired or is no longer available.</red>"),
            Map.entry("playerUnavailable", "<red>The other player is no longer available.</red>"),

            Map.entry("targetAccepted", "<green>Teleport request accepted.</green>"),
            Map.entry("sourceAccepted", "<green>${player} accepted your teleport request!</green>"),
            Map.entry("targetRefused", "<gold>Teleport request refused.</gold>"),
            Map.entry("sourceRefused", "<gold>${player} refused your teleport request!</gold>")
    );
}
