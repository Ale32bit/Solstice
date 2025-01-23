package me.alexdevs.solstice.core.coreModule.data;

import java.util.Map;

public class CoreLocale {
    public static final Map<String, String> SHARED = Map.ofEntries(
            Map.entry("button", "<click:run_command:'${command}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>"),
            Map.entry("buttonSuggest", "<click:suggest_command:'${command}'><hover:show_text:'${hoverText}'><aqua>[</aqua>${label}<aqua>]</aqua></hover></click>"),
            Map.entry("accept", "<green>Accept</green>"),
            Map.entry("refuse", "<red>Refuse</red>"),
            Map.entry("accept.hover", "Click to accept"),
            Map.entry("refuse.hover", "Click to refuse"),
            Map.entry("tooManyTargets", "<red>The provided selector contains too many targets.</red>"),
            Map.entry("cooldown", "<gold>You are on cooldown for <yellow>${timespan}</yellow>.</gold>"),
            Map.entry("unit.second", "${n} second"),
            Map.entry("unit.seconds", "${n} seconds"),
            Map.entry("unit.minute", "${n} minute"),
            Map.entry("unit.minutes", "${n} minutes"),
            Map.entry("unit.hour", "${n} hour"),
            Map.entry("unit.hours", "${n} hours"),
            Map.entry("unit.day", "${n} day"),
            Map.entry("unit.days", "${n} days"),
            Map.entry("unit.week", "${n} week"),
            Map.entry("unit.weeks", "${n} weeks"),
            Map.entry("unit.month", "${n} month"),
            Map.entry("unit.months", "${n} months"),
            Map.entry("unit.year", "${n} year"),
            Map.entry("unit.years", "${n} years")
    );

    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("stat.title", "<gold>Server Statistics</gold>"),
            Map.entry("stat.tps", "<gold>Current TPS: %server:tps_colored%<gray>/20.0</gray></gold>"),
            Map.entry("stat.uptime", "<gold>Server Uptime: <yellow>${uptime}</yellow></gold>"),
            Map.entry("stat.maxMemory", "<hover:'${hover}'><gold>Maximum memory: <yellow>${memory} MB</yellow></gold></hover>"),
            Map.entry("stat.maxMemory.hover", "How much memory the JVM can take at most in the system."),
            Map.entry("stat.dedicatedMemory", "<hover:'${hover}'><gold>Dedicated memory: <yellow>${memory} MB</yellow></gold></hover>"),
            Map.entry("stat.dedicatedMemory.hover", "How much memory the JVM is using, can expand up to maximum memory."),
            Map.entry("stat.freeMemory", "<hover:'${hover}'><gold>Free memory: <yellow>${memory} MB</yellow></gold></hover>"),
            Map.entry("stat.freeMemory.hover", "How much memory is left free in the dedicated memory.")
    );
}
