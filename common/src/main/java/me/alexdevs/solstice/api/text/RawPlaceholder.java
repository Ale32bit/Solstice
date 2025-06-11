package me.alexdevs.solstice.api.text;

import java.util.Map;
import java.util.regex.Pattern;

public class RawPlaceholder {
    public static final Pattern PATTERN = Format.PLACEHOLDER_PATTERN;

    public static String parse(String input, Map<String, String> placeholders) {
        var matcher = PATTERN.matcher(input);
        while (matcher.find()) {
            var chunk = matcher.group();
            var key = matcher.group("id");
            input = input.replace(chunk, placeholders.getOrDefault(key, ""));
        }
        return input;
    }
}
