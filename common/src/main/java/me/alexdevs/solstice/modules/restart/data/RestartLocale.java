package me.alexdevs.solstice.modules.restart.data;

import java.util.Map;

public class RestartLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("barLabel", "Server restarting in ${remaining_time}"),
            Map.entry("kickMessage", "The server is restarting!"),
            Map.entry("chatMessage", "<red>The server is restarting in </red><gold>${remaining_time}</gold>")
    );
}
