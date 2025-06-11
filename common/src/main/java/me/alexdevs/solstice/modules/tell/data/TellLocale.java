package me.alexdevs.solstice.modules.tell.data;

import java.util.Map;

public class TellLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("playerNotFound", "<red>Player <yellow>${targetPlayer}</yellow> not found!</red>"),
            Map.entry("you", "<gray><i>You</i></gray>"),
            Map.entry("message", "<gold>[</gold>${sourcePlayer} <gray>→</gray> ${targetPlayer}<gold>]</gold> ${message}"),
            Map.entry("messageSpy", "\uD83D\uDC41 <gray>[${sourcePlayer} → ${targetPlayer}] ${message}</gray>"),
            Map.entry("noLastSenderReply", "<red>You have no one to reply to.</red>") // relatable
    );
}
