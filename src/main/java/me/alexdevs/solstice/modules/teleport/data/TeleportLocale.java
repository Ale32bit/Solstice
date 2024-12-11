package me.alexdevs.solstice.modules.teleport.data;

import java.util.Map;

public class TeleportLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("teleporting", "<gold>Teleporting...</gold>"),
            Map.entry("playerNotFound", "<red>Player <yellow>${targetPlayer}</yellow> not found!</red>"),
            Map.entry("requestSent", "<gold>Teleport request sent.</gold>"),
            Map.entry("pendingTeleport", "${requesterPlayer} <gold>requested to teleport to you.</gold>\n ${acceptButton} ${refuseButton}"),
            Map.entry("pendingTeleportHere", "${requesterPlayer} <gold>requested you to teleport to them.</gold>\n ${acceptButton} ${refuseButton}"),
            Map.entry("hoverAccept", "Click to accept request"),
            Map.entry("hoverRefuse", "Click to refuse request"),
            Map.entry("noPending", "<gold>There are no pending teleport requests for you.</gold>"),
            Map.entry("unavailable", "<red>This requested expired or is no longer available.</red>"),
            Map.entry("playerUnavailable", "<red>The other player is no longer available.</red>"),
            Map.entry("requestAcceptedResult", "<green>Teleport request accepted.</green>"),
            Map.entry("requestRefusedResult", "<gold>Teleport request refused.</gold>"),
            Map.entry("requestAccepted", "<green>${player} accepted your teleport request!</green>"),
            Map.entry("requestRefused", "<gold>${player} refused your teleport request!</gold>")
    );
}
