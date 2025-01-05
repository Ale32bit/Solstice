package me.alexdevs.solstice.modules.kit.data;

import java.util.Map;

public class KitLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("claimed", "<green>Kit <yellow>${kit}</yellow> claimed!</green>"),
            Map.entry("alreadyClaimed", "<gold>You already claimed this kit!</gold>"),
            Map.entry("editKitTitle", "Edit kit: ${kit}"),
            Map.entry("edited", "<green>Kit <yellow>${kit}</yellow> edited!</green>"),
            Map.entry("newKitTitle", "New kit: ${kit}"),
            Map.entry("created", "<green>New kit <yellow>${kit}</yellow> created!</green>"),
            Map.entry("alreadyExists", "<gold>A kit with this name already exists!</gold>"),
            Map.entry("deleted", "<gold>Kit <yellow>${kit}</yellow> deleted!</gold>"),
            Map.entry("notFound", "<gold>The kit <yellow>${kit}</yellow> does not exist!</gold>"),
            Map.entry("setFirstJoin", "<gold>Set first join to <yellow>${value}</yellow> for kit <yellow>${kit}</yellow>.</gold>"),
            Map.entry("setCooldown", "<gold>Set cooldown to <yellow>${value}</yellow> for kit <yellow>${kit}</yellow>.</gold>"),
            Map.entry("setOneTime", "<gold>Set one time to <yellow>${value}</yellow> for kit <yellow>${kit}</yellow>.</gold>"),
            Map.entry("onCooldown", "<gold>You can claim this kit again in <yellow>${timespan}</yellow>!</gold>"),
            Map.entry("noPermission", "<gold>You do not have permission to claim this kit!</gold>"),
            Map.entry("listHeader", "<gold>Available kits: ${list}.</gold>"),
            Map.entry("listAvailableKit", "<run_cmd:'/kit claim ${kit}'><hover:'Click to claim'><yellow>${kit}</yellow></hover></run_cmd>"),
            Map.entry("listUnavailableKit", "<hover:'This kit is unavailable'><gray><st>${kit}</st></gray></hover>"),
            Map.entry("listComma", ", "),
            Map.entry("listNoKits", "<gold>There are no kits available.</gold>")
    );
}
