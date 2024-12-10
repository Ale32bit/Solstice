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

    @Comment("Features for chat messages.")
    public Chat chat = new Chat();

    @Comment("Configure teleport requests.")
    public TeleportRequests teleportRequests = new TeleportRequests();

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
    public static class TeleportRequests {
        @Comment("The teleport request times out after the following seconds. Defautls to 120 seconds.")
        public int teleportRequestTimeout = 120;
    }

    @ConfigSerializable
    public static class NearCommand {
        @Comment("Max range in blocks. Defaults to 48 blocks.")
        public int nearCommandMaxRange = 48;

        @Comment("Default range in blocks. Defaults to 32 blocks.")
        public int nearCommandDefaultRange = 32;
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
}
