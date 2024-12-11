package me.alexdevs.solstice.locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LocaleManager {
    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .create();

    private final Path path;
    private ConcurrentHashMap<String, String> locale;
    private final Map<String, String> defaultMap = new HashMap<>();
    private final TypeToken<?> type = TypeToken.getParameterized(Map.class, String.class, String.class);


    public LocaleManager(Path path) {
        this.path = path;
    }

    public Locale getLocale(String id) {
        return new Locale(id, () -> locale);
    }

    public void registerModule(String id, Map<String, String> defaultMap) {
        this.defaultMap.putAll(
                defaultMap.entrySet().stream().collect(Collectors.toMap(
                        entry -> "module." + id + "." + entry.getKey(),
                        Map.Entry::getValue
                ))
        );
    }

    public void registerShared(Map<String, String> defaultMap) {
        this.defaultMap.putAll(
                defaultMap.entrySet().stream().collect(Collectors.toMap(
                        entry -> "shared." + entry.getKey(),
                        Map.Entry::getValue
                ))
        );
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException {
        if (!path.toFile().exists()) {
            locale = new ConcurrentHashMap<>();
            prepare();
            return;
        }
        var bf = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8));
        locale = new ConcurrentHashMap<>((Map<String, String>) gson.fromJson(bf, type));
        prepare();
        bf.close();
    }

    public void save() throws IOException {
        var fw = new FileWriter(path.toFile(), StandardCharsets.UTF_8);
        gson.toJson(locale, fw);
        fw.close();
    }

    private void prepare() {
        if (locale == null)
            return;

        defaultMap.forEach((key, value) -> locale.putIfAbsent(key, value));
    }
}
