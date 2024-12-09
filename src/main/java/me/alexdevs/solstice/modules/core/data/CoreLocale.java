package me.alexdevs.solstice.modules.core.data;

import java.util.Map;

public class CoreLocale {
    public static final Map<String, String> SHARED = Map.ofEntries(
            Map.entry("button", "<click:run_command:'{{command}}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>"),
            Map.entry("buttonSuggest", "<click:suggest_command:'{{command}}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>"),
            Map.entry("accept", "<green>Accept</green>"),
            Map.entry("refuse", "<red>Refuse</red>"),
            Map.entry("tooManyTargets", "<red>The provided selector contains too many targets.</red>")
    );
}
