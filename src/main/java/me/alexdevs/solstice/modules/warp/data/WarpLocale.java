package me.alexdevs.solstice.modules.warp.data;

import java.util.Map;

public class WarpLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("teleporting", "<gold>Warping to <yellow>${warp}</yellow>...</gold>"),
            Map.entry("warpNotFound", "<red>The warp <yellow>${warp}</yellow> does not exist!</red>"),
            Map.entry("warpList", "<gold>Server warps: ${warpList}</gold>"),
            Map.entry("warpsFormat", "<run_cmd:'/warp ${warp}'><hover:'Click to teleport'><yellow>${warp}</yellow></hover></run_cmd>"),
            Map.entry("warpsComma", "<gold>, </gold>"),
            Map.entry("noWarps", "<gold>There are no warps so far.</gold>")
    );
}
