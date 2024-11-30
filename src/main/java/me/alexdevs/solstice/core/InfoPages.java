package me.alexdevs.solstice.core;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class InfoPages {
    private static final String[] startingPages = new String[] {
            "motd.txt",
            "rules.txt",
            "formatting.txt"
    };
    public static String nameFilterRegex = "[^a-z0-9-]";
    private static Path infoDir;

    public static void register() {
        infoDir = Solstice.configDirectory.resolve("info");
        if (!infoDir.toFile().isDirectory()) {
            if (!infoDir.toFile().mkdirs()) {
                Solstice.LOGGER.error("Couldn't create info directory");
                return;
            }

            var classLoader= Solstice.class.getClassLoader();
            var infoDirBase = "assets/" + Solstice.MOD_ID + "/info/";
            for (var name : startingPages) {
                var outputPath = infoDir.resolve(name);
                try (var inputStream = classLoader.getResourceAsStream(infoDirBase + name)){
                    if(inputStream == null) {
                        Solstice.LOGGER.warn("Missing {} info file in resources, skipping", name);
                        continue;
                    }
                    var content = inputStream.readAllBytes();
                    Files.write(outputPath, content);
                } catch (IOException e) {
                    Solstice.LOGGER.error("Could not read info file {} from resources", name, e);
                }
            }
        }
    }

    private static String sanitize(String name) {
        return name.toLowerCase().replaceAll(nameFilterRegex, "");
    }

    public static Collection<String> enumerate() {
        return Arrays.stream(Objects.requireNonNull(infoDir.toFile().listFiles()))
                .map(f -> f.getName().replace(".txt", "")).toList();
    }

    public static boolean exists(String name) {
        name = sanitize(name);
        var infoFile = infoDir.resolve(name + ".txt");
        return infoFile.toFile().exists();
    }

    public static Text getPage(String name, @Nullable PlaceholderContext context) {
        name = sanitize(name);
        if (!exists(name)) {
            return Format.parse(Solstice.locale().commands.info.pageNotFound);
        }

        var infoFile = infoDir.resolve(name + ".txt");

        try {
            // Use readAllLines instead readString to avoid \r chars; looking at you, Windows.
            var lines = Files.readAllLines(infoFile, StandardCharsets.UTF_8);
            StringBuilder content = new StringBuilder();
            for (var line : lines) {
                content.append(line).append("\n");
            }
            var output = content.toString().trim();
            if (context != null)
                return Format.parse(output, context);
            else
                return Text.of(output);
        } catch (IOException e) {
            Solstice.LOGGER.error("Could not read info file", e);
            return Format.parse(Solstice.locale().commands.info.pageError);
        }
    }
}
