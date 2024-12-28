package me.alexdevs.solstice.modules.ban.data;

import java.util.Map;

public class BanLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("banMessageFormat", "<red>You are banned from this server:</red>\n\n${reason}"),
            Map.entry("tempBanMessageFormat", "<red>You are temporarily banned from this server:</red>\n\n${reason}\n\n<gray>Expires: ${expiry_date}</gray>")
    );
}
