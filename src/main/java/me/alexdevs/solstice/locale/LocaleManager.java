package me.alexdevs.solstice.locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LocaleManager {
    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .create();

    private final Path path;

    private ConcurrentHashMap<String, String> oldLocale;
    private final Map<String, String> oldDefaultMap = new HashMap<>();
    private final TypeToken<?> oldType = TypeToken.getParameterized(Map.class, String.class, String.class);

    private LocaleModel locale;
    private final LocaleModel defaultMap = new LocaleModel();
    private final TypeToken<?> type = TypeToken.get(LocaleModel.class);

    private final Pattern sharedRegex = Pattern.compile("^shared\\.(.+)$");
    private final Pattern moduleRegex = Pattern.compile("^module\\.(\\w+)\\.(.+)$");


    public LocaleManager(Path path) {
        this.path = path;
    }

    public Locale getLocale(String id) {
        return new Locale(id, () -> oldLocale);
    }


    public void registerModule(String id, Map<String, String> defaultMap) {
        this.oldDefaultMap.putAll(
                defaultMap.entrySet().stream().collect(Collectors.toMap(
                        entry -> "module." + id + "." + entry.getKey(),
                        Map.Entry::getValue
                ))
        );


    }

    public void registerShared(Map<String, String> defaultMap) {
        this.oldDefaultMap.putAll(
                defaultMap.entrySet().stream().collect(Collectors.toMap(
                        entry -> "shared." + entry.getKey(),
                        Map.Entry::getValue
                ))
        );
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException {
        if (!path.toFile().exists()) {
            oldLocale = new ConcurrentHashMap<>();
            prepare();
            return;
        }
        var bf = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8));
        oldLocale = new ConcurrentHashMap<>((Map<String, String>) gson.fromJson(bf, oldType));
        prepare();
        bf.close();
    }

    public void save() throws IOException {
        var fw = new FileWriter(path.toFile(), StandardCharsets.UTF_8);
        gson.toJson(oldLocale, fw);
        fw.close();
    }

    private void prepare() {
        if (oldLocale == null)
            return;

        oldDefaultMap.forEach((key, value) -> oldLocale.putIfAbsent(key, value));
    }

    private void convert() {

    }

    private @Nullable LocalePath getPath(String fullPath) {
        var matcher = sharedRegex.matcher(fullPath);
        if (matcher.find()) {
            var key = matcher.group(1);
            return new LocalePath(LocaleType.SHARED, key);
        }

        matcher = moduleRegex.matcher(fullPath);
        if (matcher.find()) {
            var moduleId = matcher.group(1);
            var key = matcher.group(2);
            return new LocalePath(LocaleType.MODULE, key, moduleId);
        }

        return null;
    }

    public enum LocaleType {
        SHARED,
        MODULE
    }

    private static final class LocalePath {
        private final LocaleType type;
        private final String key;
        private final @Nullable String moduleId;

        public LocalePath(LocaleType type, String key, @Nullable String moduleId) {
            this.type = type;
            this.key = key;
            this.moduleId = moduleId;
        }

        public LocalePath(LocaleType type, String key) {
            this(type, key, null);
        }

        public LocaleType type() {
            return type;
        }

        public String key() {
            return key;
        }

        public @Nullable String moduleId() {
            return moduleId;
        }

    }

    public static class LocaleModel {
        public Map<String, String> shared = new ConcurrentHashMap<>();
        public Map<String, Map<String, String>> modules = new ConcurrentHashMap<>();
    }
}
