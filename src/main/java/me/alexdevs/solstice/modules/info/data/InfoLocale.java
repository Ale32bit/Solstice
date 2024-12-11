package me.alexdevs.solstice.modules.info.data;

import java.util.Map;

public class InfoLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("pageNotFound", "<red>This page does not exist!</red>"),
            Map.entry("pageError", "<red>There was an error opening the info page.</red>"),
            Map.entry("pageList", "<gold>Available pages: ${pageList}</gold>"),
            Map.entry("pagesFormat", "<run_cmd:'/info ${page}'><hover:'Click to read'><yellow>${page}</yellow></hover></run_cmd>"),
            Map.entry("pagesComma", "<gold>, </gold>"),
            Map.entry("noPages ", "<gold>There are no pages so far.</gold>")
    );
}
