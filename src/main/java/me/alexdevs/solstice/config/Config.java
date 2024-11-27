package me.alexdevs.solstice.config;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Config {
    public Afk afk = new Config.Afk();
    public TeleportRequests teleportRequests = new TeleportRequests();
    public Homes homes = new Homes();
    public CustomTabList customTabList = new CustomTabList();
    public NearCommand nearCommand = new NearCommand();
    public AutoRestart autoRestart = new AutoRestart();
    public CommandSpy commandSpy = new CommandSpy();
    public AutoAnnouncements autoAnnouncements = new AutoAnnouncements();
    public Motd motd = new Motd();
    public Chat chat = new Chat();

    @ConfigSerializable
    public static class Afk {
        public int afkTimeTrigger = 300;
    }

    @ConfigSerializable
    public static class TeleportRequests {
        public int teleportRequestTimeout = 120;
    }

    @ConfigSerializable
    public static class Homes {
        public int maxHomes = -1;
    }

    @ConfigSerializable
    public static class CustomTabList {
        public boolean enableTabList = true;
        public int tabListDelay = 250;
        public double tabPhasePeriod = 300;
        public ArrayList<String> tabHeader = new ArrayList<>(List.of(
                "<gradient:#DEDE6C:#CC4C4C><st>                                  </st></gradient>"
        ));

        public ArrayList<String> tabFooter = new ArrayList<>(List.of(
                "<gradient:#CC4C4C:#DEDE6C><st>                                  </st></gradient>"
        ));

        public String playerTabName = "%solstice:afk%%player:displayname_visual%";
    }

    @ConfigSerializable
    public static class NearCommand {
        public int nearCommandMaxRange = 48;
        public int nearCommandDefaultRange = 32;
    }

    @ConfigSerializable
    public static class AutoRestart {
        public boolean enableAutoRestart = true;
        public String restartBarLabel = "Server restarting in ${remaining_time}";
        public String restartKickMessage = "The server is restarting!";
        public String restartChatMessage = "<red>The server is restarting in </red><gold>${remaining_time}</gold>";

        public ArrayList<String> restartAt = new ArrayList<>(List.of(
                "06:00",
                "18:00"
        ));

        public String restartSound = "minecraft:block.note_block.bell";
        public float restartSoundPitch = 0.9f;

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
        public boolean enableChatMarkdown = true;
        public HashMap<String, String> replacements = new HashMap<>(Map.of(
                ":shrug:", "¯\\\\_(ツ)_/¯"
        ));
    }

    @ConfigSerializable
    public static class CommandSpy {
        public String commandSpyFormat = "\uD83D\uDC41 <dark_gray>${player}:</dark_gray> <gray>/${command}</gray>";
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

        public boolean enableAnnouncements = true;
        public boolean pickRandomly = false;
        // every 5 mins
        public int delay = 300;
        public ArrayList<Announcement> announcements = new ArrayList<>(List.of(
                new Announcement("Tip! <gray>Solstice is open-source! Contribute on <url:'https://github.com/Ale32bit/Solstice'><blue>GitHub</blue></url>!</gray>"),
                new Announcement("Fun fact! <gray>This announcement is only visible to players that do not have the 'solstice.example' permission granted!</gray>", "solstice.example", false)
        ));
    }

    @ConfigSerializable
    public static class Motd {
        public boolean enableMotd = true;
        public ArrayList<String> motdLines = new ArrayList<>(List.of(
                "<yellow>Welcome to the server!</yellow>"
        ));
    }
}
