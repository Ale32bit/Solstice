package me.alexdevs.solstice.locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
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
import java.util.regex.Pattern;

public class LocaleManager {
    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .create();
    private static final Pattern sharedRegex = Pattern.compile("^shared\\.(.+)$");
    private static final Pattern moduleRegex = Pattern.compile("^module\\.(\\w+\\.\\w+)\\.(.+)$");
    private final Path path;
    private final TypeToken<?> oldType = TypeToken.getParameterized(Map.class, String.class, String.class);
    private final LocaleModel defaultMap = new LocaleModel();
    private LocaleModel locale;
    private Map<String, String> localeMap;


    public LocaleManager(Path path) {
        this.path = path;
    }

    public static @Nullable LocalePath getPath(String fullPath) {
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

    public Locale getLocale(SolsticeIdentifier id) {
        return new Locale(id, () -> localeMap);
    }

    public Locale getShared() {
        return new Locale(Solstice.ID.withPath("shared"), () -> localeMap);
    }

    public void registerModule(SolsticeIdentifier id, Map<String, String> defaults) {
        var key = id.toString().replace(":", ".");
        this.defaultMap.modules.put(key, new ConcurrentHashMap<>(defaults));
    }

    public void registerShared(Map<String, String> defaults) {
        this.defaultMap.shared.putAll(defaults);
    }

    public void load() throws IOException {
        if (!path.toFile().exists()) {
            locale = new LocaleModel();
            prepare();
            localeMap = generateMap();
            return;
        }
        var bf = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8));
        locale = gson.fromJson(bf, LocaleModel.class);
        bf.close();

        if (locale.shared.isEmpty() && locale.modules.isEmpty()) {
            Solstice.LOGGER.warn("Locale casting failure. Attempting migration...");
            migrate();
        }

        migrateIdentifier();

        prepare();

        localeMap = generateMap();
    }

    public void save() throws IOException {
        var fw = new FileWriter(path.toFile(), StandardCharsets.UTF_8);
        gson.toJson(locale, fw);
        fw.close();
    }

    private void prepare() {
        if (locale == null)
            return;

        defaultMap.shared.forEach((key, value) -> locale.shared.putIfAbsent(key, value));

        //defaultMap.modules.forEach((id, map) -> locale.modules.putIfAbsent(id, new ConcurrentHashMap<>()));
        for (var defaultMods : defaultMap.modules.entrySet()) {
            var module = locale.modules.computeIfAbsent(defaultMods.getKey(), id -> new ConcurrentHashMap<>());
            for (var modLocale : defaultMap.modules.get(defaultMods.getKey()).entrySet()) {
                module.putIfAbsent(modLocale.getKey(), modLocale.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void migrate() {
        locale = new LocaleModel();
        try {
            var bf = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8));
            var oldLocale = (Map<String, String>) gson.fromJson(bf, oldType);

            for (var entry : oldLocale.entrySet()) {
                var path = getPath(entry.getKey());
                if (path == null) {
                    Solstice.LOGGER.warn("Invalid locale node: {}", entry.getKey());
                    continue;
                }

                if (path.type() == LocaleType.SHARED) {
                    locale.shared.put(path.key(), entry.getValue());
                } else if (path.type() == LocaleType.MODULE) {
                    locale.modules
                            .computeIfAbsent(path.moduleId(), key -> new ConcurrentHashMap<>())
                            .put(path.key(), entry.getValue());
                }
            }

            bf.close();

            Solstice.LOGGER.info("Successfully migrated locale!");
        } catch (IOException | JsonSyntaxException e) {
            Solstice.LOGGER.error("Could not load locale", e);
        }
    }

    private void migrateIdentifier() {
        var migrated = false;
        for (var entry : locale.modules.entrySet()) {
            var key = entry.getKey();
            if (key.contains("."))
                continue;

            var newKey = Solstice.ID.withPath(key).toString().replace(':', '.');
            locale.modules.put(newKey, entry.getValue());
            locale.modules.remove(key);
            migrated = true;
        }

        if (migrated) {
            backup();
        }
    }

    public Map<String, String> generateMap() {
        var map = new HashMap<String, String>();

        for (var entry : locale.shared.entrySet()) {
            map.put("shared." + entry.getKey(), entry.getValue());
        }

        for (var modEntry : locale.modules.entrySet()) {
            for (var entry : modEntry.getValue().entrySet()) {
                map.put("module." + modEntry.getKey() + "." + entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public void reload() throws IOException {
        load();
        save();
    }

    protected void backup() {
        var parentDir = path.getParent();
        var fileName = path.getFileName();
        var backup = parentDir.resolve(fileName.toString() + "_backup");
        if (path.toFile().renameTo(backup.toFile())) {
            Solstice.LOGGER.warn("The locale file has been migrated and the original {} has been renamed to {}!", path, backup);
        } else {
            Solstice.LOGGER.error("Could not create backup of locale file!");
        }
    }

    public enum LocaleType {
        SHARED,
        MODULE
    }

    public static final class LocalePath {
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
        public int version = 3;
        public ConcurrentHashMap<String, String> shared = new ConcurrentHashMap<>();
        public ConcurrentHashMap<String, ConcurrentHashMap<String, String>> modules = new ConcurrentHashMap<>();
    }
}
