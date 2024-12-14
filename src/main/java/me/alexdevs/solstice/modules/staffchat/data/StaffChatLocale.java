package me.alexdevs.solstice.modules.staffchat.data;

import java.util.Map;

public class StaffChatLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("message", "\uD83D\uDD27 ${name}<gray>: ${message}</gray>"),
            Map.entry("enabled", "<green>Staff chat enabled. All your messages will be sent to staff chat instead.</green>"),
            Map.entry("disabled", "<gold>Staff chat disabled.</gold>")
    );
}
