package me.alexdevs.solstice.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public class ConfigManager {
    private final HoconConfigurationLoader loader;
    private final Path path;
    private CommentedConfigurationNode root;
    private Config config;

    public ConfigManager(Path path) {
        this.path = path;
        loader = HoconConfigurationLoader.builder()
                .path(path)
                .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                .build();
    }

    public Config config() {
        return config;
    }

    public Config load() throws ConfigurateException {
        root = loader.load();
        config = root.get(Config.class);
        return config;
    }

    public void save() throws ConfigurateException {
        root.set(config);
        loader.save(root);
    }
}
