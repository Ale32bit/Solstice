package me.alexdevs.solstice.modules.help.data;

import java.util.Map;

public class HelpLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("header", "<gold>Help Page <yellow>${label}</yellow></gold>"),
            Map.entry("entry", "<gold>- <run_cmd:'/help ${name}'><yellow>${command}</yellow></run_cmd></gold>"),
            Map.entry("footer", "<gold>${previous} <yellow>${page}</yellow>/<yellow>${pages}</yellow> ${next}</gold>"),
            Map.entry("previousButton", "<run_cmd:'/help ${page}'><hover:'Previous page'><aqua>«</aqua></hover></run_cmd>"),
            Map.entry("previousButtonInactive", "<gray>«</gray>"),
            Map.entry("nextButton", "<run_cmd:'/help ${page}'><hover:'Next page'><aqua>»</aqua></hover></run_cmd>"),
            Map.entry("nextButtonInactive", "<gray>»</gray>"),
            Map.entry("notFound", "<gold>Command not found. Type <run_cmd:'/help'><yellow>/help</yellow></run_cmd> for a list of commands.</gold>")
    );
}
