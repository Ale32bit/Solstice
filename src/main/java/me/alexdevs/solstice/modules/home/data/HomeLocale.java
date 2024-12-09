package me.alexdevs.solstice.modules.home.data;

import java.util.Map;

public class HomeLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("teleporting", "<gold>Teleporting to <yellow>${home}</yellow></gold>"),
            Map.entry("teleportingOther", "<gold>Teleporting to <yellow>${owner}'s ${home}</yellow></gold>"),
            Map.entry("homeExists", "<gold>You already have set this home.</gold>\n ${forceSetButton}"),
            Map.entry("homeNotFound", "<red>The home <yellow>${home}</yellow> does not exist!</red>"),
            Map.entry("maxHomesReached", "<red>You have reached the maximum amount of homes!</red>"),
            Map.entry("homeSetSuccess", "<gold>New home <yellow>${home}</yellow> set!</gold>"),
            Map.entry("forceSetLabel", "<yellow>Force set home</yellow>"),
            Map.entry("forceSetHover", "Click to force setting new home"),
            Map.entry("homeDeleted", "<gold>Home <yellow>${home}</yellow> deleted!</gold>"),
            Map.entry("homeList", "<gold>Your homes: ${homeList}</gold>"),
            Map.entry("homeListOther", "<gold><yellow>${owner}</yellow>'s homes: ${homeList}</gold>"),
            Map.entry("homesFormat", "<run_cmd:'/home ${home}'><hover:'Click to teleport'><yellow>${home}</yellow></hover></run_cmd>"),
            Map.entry("homesFormatOther", "<run_cmd:'/homeother ${owner} ${home}'><hover:'Click to teleport'><yellow>${home}</yellow></hover></run_cmd>"),
            Map.entry("homesComma", "<gold>, </gold>"),
            Map.entry("noHomes", "<gold>You did not set any home so far.</gold>"),
            Map.entry("noHomesOther", "<gold><yellow>${owner}</yellow> did not set any home so far.</gold>")
    );
}
