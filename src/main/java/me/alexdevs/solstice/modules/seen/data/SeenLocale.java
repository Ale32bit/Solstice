package me.alexdevs.solstice.modules.seen.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeenLocale {
    private static final ArrayList<String> base = new ArrayList<>(List.of(
            "<yellow>${username}</yellow><gold>'s information:</gold>",
            " <gold>UUID:</gold> <yellow>${uuid}</yellow>",
            " <gold>First seen:</gold> <yellow>${firstSeenDate}</yellow>",
            " <gold>Last seen:</gold> <yellow>${lastSeenDate}</yellow>"
    ));
    private static final ArrayList<String> extended = new ArrayList<>(List.of(
            " <gold>IP Address:</gold> <yellow>${ipAddress}</yellow>",
            " <gold>Location:</gold> <yellow>${location}</yellow>"
    ));

    public static final Map<String, String> MODULE;

    static {
        var map = new HashMap<String, String>();
        map.put("playerNotFound", "<red>Could not find this player</red>");
        map.put("base", String.join("\n", base));
        map.put("extended", String.join("\n", extended));

        MODULE = Map.copyOf(map);
    }
}
