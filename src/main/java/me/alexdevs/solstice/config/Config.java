package me.alexdevs.solstice.config;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Config {
    @Comment("Customize various texts formatting.")
    public Formats formats = new Formats();

    @Comment("Customize the header and footer of the player tab list.")
    public CustomTabList customTabList = new CustomTabList();

    @Comment("Periodically broadcast announcements, with optional permission condition.")
    public AutoAnnouncements autoAnnouncements = new AutoAnnouncements();

    @Comment("Configure automatic server restart.")
    public AutoRestart autoRestart = new AutoRestart();

    @Comment("Configure the messages of the day, display to all players when joining the server.")
    public Motd motd = new Motd();

    @Comment("Features for chat messages.")
    public Chat chat = new Chat();

    @Comment("Enable listening to all commands ran by players.")
    public CommandSpy commandSpy = new CommandSpy();

    @Comment("Configure the AFK (away from keyboard) system.")
    public Afk afk = new Afk();

    @Comment("Configure teleport requests.")
    public TeleportRequests teleportRequests = new TeleportRequests();

    @Comment("Configure player homes")
    public Homes homes = new Homes();

    @Comment("Configure the /near command.")
    public NearCommand nearCommand = new NearCommand();

    @ConfigSerializable
    public static class Formats {
        @ConfigSerializable
        public record NameFormat(String group, String format) {
        }

        @ConfigSerializable
        public static class AdvancementFormats {
            public String task = "<green>✔</green> %player:displayname% <gray>completed the task</gray> <hover:'${description}'><green>${title}</green></hover>";
            public String challenge = "<light_purple>\uD83C\uDF86</light_purple> %player:displayname% <gray>completed the challenge</gray> <hover:'${description}'><light_purple>${title}</light_purple></hover>";
            public String goal = "<aqua>\uD83C\uDF96</aqua> %player:displayname% <gray>completed the goal</gray> <hover:'${description}'><aqua>${title}</aqua></hover>";
        }

        @Comment("Customize player display names based on their LuckPerms group.")
        public ArrayList<NameFormat> nameFormats = new ArrayList<>(List.of(
                new NameFormat("admin", "<red>${name}</red>"),
                new NameFormat("default", "<green>${name}</green>")
        ));

        @Comment("Player chat message format.")
        public String chatFormat = "%player:displayname%<gray>:</gray> ${message}";

        @Comment("Player '/me' message format.")
        public String emoteFormat = "<gray>\uD83D\uDC64 %player:displayname% <i>${message}</i></gray>";

        @Comment("Player join message format.")
        public String joinFormat = "<green>+</green> %player:displayname% <yellow>joined!</yellow>";

        @Comment("Player join with a different username message format.")
        public String joinRenamedFormat = "<green>+</green> %player:displayname% <yellow>joined! <i>(Previously known as ${previousName})</i></yellow>";

        @Comment("Player left message format.")
        public String leaveFormat = "<red>-</red> %player:displayname% <yellow>left!</yellow>";

        @Comment("Player death message format.")
        public String deathFormat = "<gray>\u2620 ${message}</gray>";

        @Comment("Player made an announced advancement.")
        public AdvancementFormats advancementFormats = new AdvancementFormats();

        @Comment("Generic date format to use.\nMetric format: dd/MM/yyyy\nUSA format: MM/dd/yyyy")
        public String dateFormat = "dd/MM/yyyy";

        @Comment("Generic time format to use.\n24h format: HH:mm\n12h format: hh:mm a")
        public String timeFormat = "HH:mm";

        @Comment("Generic date + time format to use.")
        public String dateTimeFormat = "dd/MM/yyyy HH:mm";

        @Comment("Format to use when displaying links in chat.")
        public String link = "<c:#8888ff><u>${label}</u></c>";

        @Comment("Format to use when hovering over the link in chat.")
        public String linkHover = "${url}";

        @Comment("Format the ban message when a banned player attempts to join.")
        public String banMessageFormat = "<red>You are banned from this server:</red>\n\n${reason}";

        @Comment("Like the banMessageFormat, but when temporary.")
        public String tempBanMessageFormat = "<red>You are temporary banned from this server:</red>\n\n${reason}\n\n<gray>Expires: ${expiry_date}</gray>";

    }

    @ConfigSerializable
    public static class Afk {
        @Comment("Enable the AFK functionality.")
        public boolean enableAfk = true;

        @Comment("Announce in chat when a player goes or return from AFK.")
        public boolean announceAfk = true;

        @Comment("AFK triggers after the player has been inactive for the following seconds. Defaults to 300 seconds.")
        public int afkTimeTrigger = 300;
    }

    @ConfigSerializable
    public static class TeleportRequests {
        @Comment("The teleport request times out after the following seconds. Defautls to 120 seconds.")
        public int teleportRequestTimeout = 120;
    }

    @ConfigSerializable
    public static class Homes {
        @Comment("Limit how many homes a player can set. -1 means unlimited homes. Defaults to -1.")
        public int maxHomes = -1;
    }

    @ConfigSerializable
    public static class CustomTabList {
        @Comment("Enable the custom tab list functionality.")
        public boolean enableTabList = true;

        @Comment("Send tab list updates every X milliseconds. Defaults to 250 ms.")
        public int tabListDelay = 250;

        @Comment("How fast the phase is. Lower is faster. Defaults to 300")
        public double tabPhasePeriod = 300;

        @Comment("Header lines")
        public ArrayList<String> tabHeader = new ArrayList<>(List.of(
                "<gradient:#DEDE6C:#CC4C4C><st>                                  </st></gradient>"
        ));

        @Comment("Footer lines")
        public ArrayList<String> tabFooter = new ArrayList<>(List.of(
                "<gradient:#CC4C4C:#DEDE6C><st>                                  </st></gradient>"
        ));

        @Comment("Format to use when displaying the player name in the tab list.")
        public String playerTabName = "%solstice:afk%%player:displayname_visual%";
    }

    @ConfigSerializable
    public static class NearCommand {
        @Comment("Max range in blocks. Defaults to 48 blocks.")
        public int nearCommandMaxRange = 48;

        @Comment("Default range in blocks. Defaults to 32 blocks.")
        public int nearCommandDefaultRange = 32;
    }

    @ConfigSerializable
    public static class AutoRestart {
        @Comment("Enable auto restart functionality.")
        public boolean enableAutoRestart = true;

        @Comment("Label to use on top of the boss bar when about to restart.\nUse the following placeholders in the '${nameHere}' format:\n remaining_time: time remaining.\n elapsed_time: time elapsed.\n total_time: total time of the countdown.")
        public String restartBarLabel = "Server restarting in ${remaining_time}";

        @Comment("Message to show when kicking players right before restart.")
        public String restartKickMessage = "The server is restarting!";

        @Comment("Message to show in chat when a remaining time \"milestone\" is hit.")
        public String restartChatMessage = "<red>The server is restarting in </red><gold>${remaining_time}</gold>";

        @Comment("Restart the server at exactly the following times. Time is local.")
        public ArrayList<String> restartAt = new ArrayList<>(List.of(
                "06:00",
                "18:00"
        ));

        @Comment("Sound to play when sending the restart notification in chat.")
        public String restartSound = "minecraft:block.note_block.bell";

        @Comment("Pitch of the sound.")
        public float restartSoundPitch = 0.9f;

        @Comment("Milestones of the restart notifications in seconds.")
        public ArrayList<Integer> restartNotifications = new ArrayList<>(List.of(
                600,
                300,
                120,
                60,
                30,
                15,
                10,
                5,
                4,
                3,
                2,
                1
        ));
    }

    @ConfigSerializable
    public static class Chat {
        @Comment("Enable Markdown support in chat.")
        public boolean enableChatMarkdown = true;

        @Comment("Replace text chunks in chat messages.")
        public HashMap<String, String> replacements = new HashMap<>(Map.of(
                ":shrug:", "¯\\\\_(ツ)_/¯"
        ));
    }

    @ConfigSerializable
    public static class CommandSpy {
        @Comment("Format to use when notifying a command.")
        public String commandSpyFormat = "\uD83D\uDC41 <dark_gray>${player}:</dark_gray> <gray>/${command}</gray>";

        @Comment("Commands to ignore.")
        public ArrayList<String> ignoredCommands = new ArrayList<>(List.of(
                "tell", "w", "msg", "dm", "r"
        ));
    }

    @ConfigSerializable
    public static class AutoAnnouncements {
        @ConfigSerializable
        public record Announcement(String text, @Nullable String permission, @Nullable Boolean result) {
            public Announcement(String text) {
                this(text, null, null);
            }
        }

        @Comment("Enable automatic announcements functionality.")
        public boolean enableAnnouncements = true;

        @Comment("Pick the next announcement randomly, else linearly.")
        public boolean pickRandomly = false;
        // every 5 mins
        @Comment("Send announcement every X seconds. Defaults to 300 seconds.")
        public int delay = 300;

        @Comment("Announcement list. Announcements can have a permission as condition. If result is true, the permission has to be granted, else the permission has to be denied (or unset).")
        public ArrayList<Announcement> announcements = new ArrayList<>(List.of(
                new Announcement("Tip! <gray>Solstice is open-source! Contribute on <url:'https://github.com/Ale32bit/Solstice'><blue>GitHub</blue></url>!</gray>"),
                new Announcement("Fun fact! <gray>This announcement is only visible to players that do not have the 'solstice.example' permission granted!</gray>", "solstice.example", false)
        ));
    }

    @ConfigSerializable
    public static class Motd {
        @Comment("Enable the MOTD functionality.")
        public boolean enableMotd = true;

        @Comment("The message. Every line is a line...")
        public ArrayList<String> motdLines = new ArrayList<>(List.of(
                "<yellow>Welcome to the server!</yellow>"
        ));
    }
}
