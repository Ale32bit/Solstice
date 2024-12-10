package me.alexdevs.solstice.config.locale;

import java.util.ArrayList;
import java.util.List;

public class Locale {

    public String youAreMuted = "<gold>You are muted!</gold>";

    public Commands commands = new Commands();

    public static class Commands {
        public Common common = new Common();
        public Near near = new Near();
        public TeleportRequest teleportRequest = new TeleportRequest();
        public Tell tell = new Tell();
        public Seen seen = new Seen();

        public static class Common {
            // `{{command}}` is replaced as a string before parsing
            public String button = "<click:run_command:'{{command}}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>";
            public String buttonSuggest = "<click:suggest_command:'{{command}}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>";
            public String accept = "<green>Accept</green>";
            public String refuse = "<red>Refuse</red>";
        }

        public static class Near {
            public String noOne = "<gold>There are no players near you.</gold>";
            public String nearestPlayers = "<gold>Nearest players: ${playerList}</gold>";
            public String format = "${player} <gold>(</gold><yellow>${distance}</yellow><gold>)</gold>";
            public String comma = "<gold>, </gold>";
        }

        public static class TeleportRequest {
            public String teleporting = "<gold>Teleporting...</gold>";
            public String playerNotFound = "<red>Player <yellow>${targetPlayer}</yellow> not found!</red>";
            public String requestSent = "<gold>Teleport request sent.</gold>";
            public String pendingTeleport = "${requesterPlayer} <gold>requested to teleport to you.</gold>\n ${acceptButton} ${refuseButton}";
            public String pendingTeleportHere = "${requesterPlayer} <gold>requested you to teleport to them.</gold>\n ${acceptButton} ${refuseButton}";
            public String hoverAccept = "Click to accept request";
            public String hoverRefuse = "Click to refuse request";
            public String noPending = "<gold>There are no pending teleport requests for you.</gold>";
            public String unavailable = "<red>This requested expired or is no longer available.</red>";
            public String playerUnavailable = "<red>The other player is no longer available.</red>";
            public String requestAcceptedResult = "<green>Teleport request accepted.</green>";
            public String requestRefusedResult = "<gold>Teleport request refused.</gold>";
            public String requestAccepted = "<green>${player} accepted your teleport request!</green>";
            public String requestRefused = "<gold>${player} refused your teleport request!</gold>";
        }

        public static class Tell {
            public String playerNotFound = "<red>Player <yellow>${targetPlayer}</yellow> not found!</red>";
            public String you = "<gray><i>You</i></gray>";
            public String message = "<gold>[</gold>${sourcePlayer} <gray>→</gray> ${targetPlayer}<gold>]</gold> ${message}";
            public String messageSpy = "\uD83D\uDC41 <gray>[${sourcePlayer} → ${targetPlayer}] ${message}</gray>";
            public String noLastSenderReply = "<red>You have no one to reply to.</red>"; // relatable
        }

        public static class Seen {
            public String playerNotFound = "<red>Could not find this player</red>";
            public ArrayList<String> base = new ArrayList<>(List.of(
                    "<yellow>${username}</yellow><gold>'s information:</gold>",
                    " <gold>UUID:</gold> <yellow>${uuid}</yellow>",
                    " <gold>First seen:</gold> <yellow>${firstSeenDate}</yellow>",
                    " <gold>Last seen:</gold> <yellow>${lastSeenDate}</yellow>"
            ));
            public ArrayList<String> extended = new ArrayList<>(List.of(
                    " <gold>IP Address:</gold> <yellow>${ipAddress}</yellow>",
                    " <gold>Location:</gold> <yellow>${location}</yellow>"
            ));
        }
    }
}
