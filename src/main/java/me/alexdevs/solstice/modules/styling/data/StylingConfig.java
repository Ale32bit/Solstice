package me.alexdevs.solstice.modules.styling.data;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class StylingConfig {
    @Comment("Enable Markdown support in chat.")
    public boolean enableMarkdown = true;

    @Comment("Whether to broadcast a welcome message to everyone when a player joins for the first time.")
    public boolean welcomeNewPlayers = true;

    @Comment("Replace text chunks in chat messages.")
    public HashMap<String, String> replacements = new HashMap<>(Map.of(
            ":shrug:", "¯\\\\_(ツ)_/¯"
    ));

    @Comment("Task advancement format.")
    public String advancementTask = "<green>✔</green> %player:displayname% <gray>completed the task</gray> <hover:'${description}'><green>${title}</green></hover>";

    @Comment("Challenge advancement format.")
    public String advancementChallenge = "<light_purple>\uD83C\uDF86</light_purple> %player:displayname% <gray>completed the challenge</gray> <hover:'${description}'><light_purple>${title}</light_purple></hover>";

    @Comment("Goal advancement format.")
    public String advancementGoal = "<aqua>\uD83C\uDF96</aqua> %player:displayname% <gray>completed the goal</gray> <hover:'${description}'><aqua>${title}</aqua></hover>";

    @Comment("!! This setting is deprecated. Use chat-formats instead. !!")
    @Deprecated
    public @Nullable String chatFormat = null;

    @Comment("Chat format per group. group = format")
    public Map<String, String> chatFormats = Map.of(
            "default", "%player:displayname%<gray>:</gray> ${message}"
    );

    @Comment("Nameplate formatting per group. group = format.\nPlaceholders here are not refreshed often.")
    public Map<String, NameplateFormat> nameplateFormats = Map.of(
            "default", new NameplateFormat("", "", "green")
    );

    @Comment("Emote format (/me)")
    public String emoteFormat = "<gray>\uD83D\uDC64 %player:displayname% <i>${message}</i></gray>";

    @Comment("Player join format")
    public String joinFormat = "<green>+</green> %player:displayname% <yellow>joined!</yellow>";

    @Comment("Player joined with a new username format")
    public String joinRenamedFormat = "<green>+</green> %player:displayname% <yellow>joined! <i>(Previously known as ${previousName})</i></yellow>";

    @Comment("Player quit format")
    public String leaveFormat = "<red>-</red> %player:displayname% <yellow>left!</yellow>";

    @Comment("Player death format")
    public String deathFormat = "<gray>\u2620 ${message}</gray>";

    @Comment("New player welcome message format")
    public String welcome = "<light_purple>Welcome %player:displayname% to the server!</light_purple>";

    @ConfigSerializable
    public record NameplateFormat(String prefix, String suffix, String color) { }

}
