package me.alexdevs.solstice.modules.info;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Paths;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.info.commands.InfoCommand;
import me.alexdevs.solstice.modules.info.commands.MotdCommand;
import me.alexdevs.solstice.modules.info.commands.RulesCommand;
import me.alexdevs.solstice.modules.info.data.InfoConfig;
import me.alexdevs.solstice.modules.info.data.InfoLocale;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class InfoModule extends ModuleBase {
    private static final String[] startingPages = new String[]{
            "motd.txt",
            "rules.txt",
            "formatting.txt"
    };
    public final String nameFilterRegex = "[^a-z0-9-]";
    private final Path infoDir;

    public InfoModule(ModuleProperties properties) {
        super(properties);
        infoDir = Paths.configDirectory.resolve("info");
    }

    @Override
    public void init() {
        registerConfig(InfoConfig.class, InfoConfig::new);
        registerLocale(InfoLocale.MODULE);

        commands.add(new InfoCommand(this));
        commands.add(new MotdCommand(this));
        commands.add(new RulesCommand(this));

        if (!infoDir.toFile().isDirectory()) {
            if (!infoDir.toFile().mkdirs()) {
                Solstice.LOGGER.error("Couldn't create info directory");
                return;
            }

            var classLoader = Solstice.class.getClassLoader();
            var infoDirBase = "assets/" + this.id.getNamespace() + "/info/";
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
            if (getConfig().enableMotd) {
                if (!exists("motd")) {
                    Solstice.LOGGER.warn("Could not send MOTD because info/motd.txt does not exist!");
                    return;
                }
                Solstice.nextTick(() -> {
                    var motd = buildMotd(PlaceholderContext.of(handler.getPlayer()));
                    handler.getPlayer().sendSystemMessage(motd);
                });
            }
        });
    }

    public InfoConfig getConfig() {
        return Solstice.configManager.getData(InfoConfig.class);
    }

    public Component buildMotd(PlaceholderContext context) {
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

    public Component getPage(String name, @Nullable PlaceholderContext context) {
        name = sanitize(name);
        if (!exists(name)) {
            return locale().get("pageNotFound");
        }

        var infoFile = infoDir.resolve(name + ".txt");

        try {
            // Use readAllLines instead of readString to avoid \r chars; looking at you, Windows.
            var lines = Files.readAllLines(infoFile, StandardCharsets.UTF_8);
            StringBuilder content = new StringBuilder();
            for (var line : lines) {
                content.append(line).append("\n");
            }
            var output = content.toString().trim();
            if (context != null)
                return Format.parse(output, context);
            else
                return Component.nullToEmpty(output);
        } catch (IOException e) {
            Solstice.LOGGER.error("Could not read info file", e);
            return locale().get("pageError");
        }
    }
}
