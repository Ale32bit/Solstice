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

public class ToggleableConfig {
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
        return modules.computeIfAbsent(id, (i) -> true);
    }

    private void load() {
        if (!this.filePath.toFile().exists()) {
            return;
        }
        try (var br = new BufferedReader(new FileReader(this.filePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                var parts = line.split("=");
                if (parts.length != 2) {
                    continue;
                }

                var key = parts[0].trim();
                var value = parts[1].trim();

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
