package me.alexdevs.solstice.modules.info;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.info.commands.InfoCommand;
import me.alexdevs.solstice.modules.info.commands.MotdCommand;
import me.alexdevs.solstice.modules.info.commands.RulesCommand;
import me.alexdevs.solstice.modules.info.data.InfoConfig;
import me.alexdevs.solstice.modules.info.data.InfoLocale;
import me.alexdevs.solstice.util.Format;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class InfoModule {
    public static final String ID = "info";

    private static final String[] startingPages = new String[]{
            "motd.txt",
            "rules.txt",
            "formatting.txt"
    };
    public final String nameFilterRegex = "[^a-z0-9-]";
    private final Path infoDir;

    private final InfoConfig config;
    public final Locale locale;

    public InfoModule() {
        Solstice.configManager.registerData(ID, InfoConfig.class, InfoConfig::new);
        Solstice.localeManager.registerModule(ID, InfoLocale.MODULE);

        config = Solstice.configManager.getData(InfoConfig.class);
        locale = Solstice.localeManager.getLocale(ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new InfoCommand(dispatcher, registryAccess, environment);
            new MotdCommand(dispatcher, registryAccess, environment);
            new RulesCommand(dispatcher, registryAccess, environment);
        });

        infoDir = Solstice.configDirectory.resolve("info");
        if (!infoDir.toFile().isDirectory()) {
            if (!infoDir.toFile().mkdirs()) {
                Solstice.LOGGER.error("Couldn't create info directory");
                return;
            }

            var classLoader = Solstice.class.getClassLoader();
            var infoDirBase = "assets/" + Solstice.MOD_ID + "/info/";
            for (var name : startingPages) {
                var outputPath = infoDir.resolve(name);
                try (var inputStream = classLoader.getResourceAsStream(infoDirBase + name)) {
                    if (inputStream == null) {
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

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (config.enableMotd) {
                if (!exists("motd")) {
                    Solstice.LOGGER.warn("Could not send MOTD because info/motd.txt does not exist!");
                    return;
                }
                var motd = buildMotd(PlaceholderContext.of(handler.getPlayer()));
                Solstice.nextTick(() -> handler.getPlayer().sendMessage(motd));
            }
        });
    }

    public Text buildMotd(PlaceholderContext context) {
        return getPage("motd", context);
    }


    private String sanitize(String name) {
        return name.toLowerCase().replaceAll(nameFilterRegex, "");
    }

    public Collection<String> enumerate() {
        return Arrays.stream(Objects.requireNonNull(infoDir.toFile().listFiles()))
                .map(f -> f.getName().replace(".txt", "")).toList();
    }

    public boolean exists(String name) {
        name = sanitize(name);
        var infoFile = infoDir.resolve(name + ".txt");
        return infoFile.toFile().exists();
    }

    public Text getPage(String name, @Nullable PlaceholderContext context) {
        name = sanitize(name);
        if (!exists(name)) {
            return locale.get("pageNotFound");
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
            return locale.get("pageError");
        }
    }
}
