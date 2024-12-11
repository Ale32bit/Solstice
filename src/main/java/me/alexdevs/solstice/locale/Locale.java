package me.alexdevs.solstice.locale;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.util.Format;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Locale {
    public final String id;

    private Supplier<ConcurrentHashMap<String, String>> localeSupplier;

    public Locale(String id, Supplier<ConcurrentHashMap<String, String>> localeSupplier) {
        this.id = id;
        this.localeSupplier = localeSupplier;
    }

    public String raw(String path) {
        var locale = this.localeSupplier.get();
        String fullPath;
        if (path.startsWith("~")) {
            fullPath = "shared." + path.substring(1);
        } else if (path.startsWith("/")) {
            fullPath = path.substring(1);
        } else {
            fullPath = "module." + this.id + "." + path;
        }

        if (locale.containsKey(fullPath)) {
            return locale.get(fullPath);
        }

        return fullPath;
    }

    public Text get(String path) {
        var src = this.raw(path);
        return Format.parse(src);
    }

    public Text get(String path, PlaceholderContext context) {
        var src = this.raw(path);
        return Format.parse(src, context);
    }

    public Text get(String path, Map<String, Text> placeholders) {
        var src = this.raw(path);
        return Format.parse(src, placeholders);
    }

    public Text get(String path, PlaceholderContext context, Map<String, Text> placeholders) {
        var src = this.raw(path);
        return Format.parse(src, context, placeholders);
    }
}
