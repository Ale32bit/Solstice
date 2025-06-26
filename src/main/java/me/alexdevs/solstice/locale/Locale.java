package me.alexdevs.solstice.locale;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.text.Format;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class Locale {
    public final ResourceLocation id;

    private final Supplier<LocaleManager.LocaleModel> localeSupplier;

    public Locale(ResourceLocation id, Supplier<LocaleManager.LocaleModel> localeSupplier) {
        this.id = id;
        this.localeSupplier = localeSupplier;
    }

    public String raw(String path) {
        String fullPath;
        if (path.startsWith("~")) {
            fullPath = "shared." + path.substring(1);
        } else if (path.startsWith("/")) {
            fullPath = path.substring(1);
        } else {
            var id = this.id.toString().replace(':', '.');
            fullPath = "module." + id + "." + path;
        }

        return localeSupplier.get().get(fullPath);
    }

    public Component get(String path) {
        var src = this.raw(path);
        return Format.parse(src);
    }

    public Component get(String path, PlaceholderContext context) {
        var src = this.raw(path);
        return Format.parse(src, context);
    }

    public Component get(String path, Map<String, Component> placeholders) {
        var src = this.raw(path);
        return Format.parse(src, placeholders);
    }

    public Component get(String path, PlaceholderContext context, Map<String, Component> placeholders) {
        var src = this.raw(path);
        return Format.parse(src, context, placeholders);
    }
}
