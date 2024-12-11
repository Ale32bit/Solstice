package me.alexdevs.solstice.modules.styling.data;

import java.util.Map;

public class StylingLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("advancementTask", "<green>✔</green> %player:displayname% <gray>completed the task</gray> <hover:'${description}'><green>${title}</green></hover>"),
            Map.entry("advancementChallenge", "<light_purple>\uD83C\uDF86</light_purple> %player:displayname% <gray>completed the challenge</gray> <hover:'${description}'><light_purple>${title}</light_purple></hover>"),
            Map.entry("advancementGoal", "<aqua>\uD83C\uDF96</aqua> %player:displayname% <gray>completed the goal</gray> <hover:'${description}'><aqua>${title}</aqua></hover>"),
            Map.entry("chatFormat", "%player:displayname%<gray>:</gray> ${message}"),
            Map.entry("emoteFormat", "<gray>\uD83D\uDC64 %player:displayname% <i>${message}</i></gray>"),
            Map.entry("joinFormat", "<green>+</green> %player:displayname% <yellow>joined!</yellow>"),
            Map.entry("joinRenamedFormat", "<green>+</green> %player:displayname% <yellow>joined! <i>(Previously known as ${previousName})</i></yellow>"),
            Map.entry("leaveFormat", "<red>-</red> %player:displayname% <yellow>left!</yellow>"),
            Map.entry("deathFormat", "<gray>\u2620 ${message}</gray>")

    );
}
