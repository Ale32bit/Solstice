package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Paths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ToggleableConfig {
    public static final Pattern ENTRY_PATTERN = Pattern.compile("^\\s*(?<key>\\w+(?:[.:]\\w+)?)=(?<value>\\w+)");

    private static ToggleableConfig instance = null;

    private final Map<String, Boolean> modules = new HashMap<>();
    private final Path filePath;

    public static ToggleableConfig get() {
        if (instance == null) {
            instance = new ToggleableConfig(Paths.configDirectory.resolve("modules.conf"));
        }
        return instance;
    }

    ToggleableConfig(Path filePath) {
        this.filePath = filePath;
        load();
    }

    public boolean isEnabled(String id) {
        id = id.replace(':', '.');
        if (!id.contains(".")) {
            id = "solstice." + id;
        }
        return modules.computeIfAbsent(id, (i) -> true);
    }

    private void load() {
        if (!this.filePath.toFile().exists()) {
            return;
        }
        try (var br = new BufferedReader(new FileReader(this.filePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                var matcher = ENTRY_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    if (!line.isBlank() && !line.startsWith("#")) {
                        System.out.println("Malformed module config entry in solstice/modules.conf: " + line + ". Skipping.");
                    }
                    continue;
                }
                var key = matcher.group("key").replace(':', '.');
                if (!key.contains(".")) {
                    key = "solstice." + key;
                }
                var value = matcher.group("value");
                var enabled = Boolean.parseBoolean(value);
                modules.put(key, enabled);
            }
        } catch (Exception e) {
            System.out.println("Error loading toggleable state of modules. Assuming all enabled. " + e.getMessage());
        }
    }

    public void save() {
        var list = modules.entrySet().stream().map(e -> new Entry(e.getKey(), e.getValue())).sorted(Comparator.comparing(Entry::id)).toList();
        try (var bw = new BufferedWriter(new FileWriter(this.filePath.toFile()))) {
            for (var entry : list) {
                bw.write(entry.id() + "=" + entry.enabled());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error saving toggleable state of modules. Assuming all enabled in the next load." + e.getMessage());
        }
    }

    private record Entry(String id, boolean enabled) {
    }
}
