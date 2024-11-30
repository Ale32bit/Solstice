package me.alexdevs.solstice.config.locale;

import java.util.ArrayList;
import java.util.List;

public class Locale {

    public String youAreMuted = "<gold>You are muted!</gold>";

    public Commands commands = new Commands();

    public static class Commands {
        public Common common = new Common();
        public Back back = new Back();
        public Near near = new Near();
        public Home home = new Home();
        public Spawn spawn = new Spawn();
        public TeleportRequest teleportRequest = new TeleportRequest();
        public Tell tell = new Tell();
        public Warp warp = new Warp();
        public Afk afk = new Afk();
        public Mail mail = new Mail();
        public Seen seen = new Seen();
        public Ignore ignore = new Ignore();
        public Info info = new Info();

        public static class Common {
            // `{{command}}` is replaced as a string before parsing
            public String button = "<click:run_command:'{{command}}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>";
            public String buttonSuggest = "<click:suggest_command:'{{command}}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>";
            public String accept = "<green>Accept</green>";
            public String refuse = "<red>Refuse</red>";
        }

        public static class Back {
            public String teleporting = "<gold>Teleporting to previous position...</gold>";
            public String noPosition = "<red>There is no position to return back to.</red>";
        }

        public static class Near {
            public String noOne = "<gold>There are no players near you.</gold>";
            public String nearestPlayers = "<gold>Nearest players: ${playerList}</gold>";
            public String format = "${player} <gold>(</gold><yellow>${distance}</yellow><gold>)</gold>";
            public String comma = "<gold>, </gold>";
        }

        public static class Home {
            public String teleporting = "<gold>Teleporting to <yellow>${home}</yellow></gold>";
            public String homeExists = "<gold>You already have set this home.</gold>\n ${forceSetButton}";
            public String homeNotFound = "<red>The home <yellow>${home}</yellow> does not exist!</red>";
            public String maxHomesReached = "<red>You have reached the maximum amount of homes!</red>";
            public String homeSetSuccess = "<gold>New home <yellow>${home}</yellow> set!</gold>";
            public String forceSetLabel = "<yellow>Force set home</yellow>";
            public String forceSetHover = "Click to force setting new home";
            public String homeDeleted = "<gold>Home <yellow>${home}</yellow> deleted!</gold>";
            public String homeList = "<gold>Your homes: ${homeList}</gold>";
            public String homesFormat = "<run_cmd:'/home ${home}'><hover:'Click to teleport'><yellow>${home}</yellow></hover></run_cmd>";
            public String homesComma = "<gold>, </gold>";
            public String noHomes = "<gold>You did not set any home so far.</gold>";
        }

        public static class Spawn {
            public String teleporting = "<gold>Teleporting to spawn...</gold>";
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

        public static class Warp {
            public String teleporting = "<gold>Warping to <yellow>${warp}</yellow>...</gold>";
            public String warpNotFound = "<red>The warp <yellow>${warp}</yellow> does not exist!</red>";
            public String warpList = "<gold>Server warps: ${warpList}</gold>";
            public String warpsFormat = "<run_cmd:'/warp ${warp}'><hover:'Click to teleport'><yellow>${warp}</yellow></hover></run_cmd>";
            public String warpsComma = "<gold>, </gold>";
            public String noWarps = "<gold>There are no warps so far.</gold>";
        }

        public static class Afk {
            public String goneAfk = "<gray>%player:displayname% is now AFK</gray>";
            public String returnAfk = "<gray>%player:displayname% is no longer AFK</gray>";
            public String tag = "<gray>[AFK]</gray> ";
        }

        public static class Mail {
            public String mailPending = "<green>You have pending mails! Run <click:run_command:'/mail'><hover:show_text:Click to read mails><aqua>/mail</aqua></hover></click> to read your mails.</green>";
            public String replyButton = "<gold>Reply</gold>";
            public String deleteButton = "<red>Delete</red>";
            public String readButton = "<gold>Read</gold>";
            public String hoverReply = "Click to reply to the mail";
            public String hoverDelete = "Click to delete the mail";
            public String hoverRead = "Click to read the mail";
            public String playerNotFound = "<red>Player <yellow>${recipient}</yellow> not found!</red>";
            public String mailSent = "<gold>Mail sent!</gold>";
            public String mailReceived = "<gold>You received a new mail! Run <click:run_command:'/mail'><hover:show_text:Click to read mails><aqua>/mail</aqua></hover></click> to read the emails!</gold>";
            public String mailDetails = "<gold>From</gold> <yellow>${sender}</yellow> <gold>on</gold> <yellow>${date}</yellow>\n ${message}\n\n ${replyButton} ${deleteButton}";
            public String mailListHeader = "<gold>Your mails:</gold>";
            public String mailListEntry = "<yellow>${index}.</yellow> <gold>From</gold> <yellow>${sender}</yellow> <gold>on</gold> <yellow>${date}</yellow> ${readButton}";
            public String notFound = "<red>Mail not found</red>";
            public String mailDeleted = "<gold>Mail deleted!</gold>";
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

        public static class Ignore {
            public String playerNotFound = "<red>Could not find this player</red>";
            public String targetIsSelf = "<red>You cannot ignore yourself.</red>";
            public String blockedPlayer = "<yellow>${targetName}</yellow> <gold>is now ignored.</gold>";
            public String unblockedPlayer = "<yellow>${targetName}</yellow> <green>is no longer ignored.</green>";
        }

        public static class Info {
            public String pageNotFound = "<red>This page does not exist!</red>";
            public String pageError = "<red>There was an error opening the info page.</red>";
            public String pageList = "<gold>Available pages: ${pageList}</gold>";
            public String pagesFormat = "<run_cmd:'/info ${page}'><hover:'Click to read'><yellow>${page}</yellow></hover></run_cmd>";
            public String pagesComma = "<gold>, </gold>";
            public String noPages  = "<gold>There are no pages so far.</gold>";
        }
    }
}
