package me.alexdevs.solstice.util.data;

import io.leangen.geantyref.TypeToken;
import me.alexdevs.solstice.Solstice;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GsonDataManager {
    private final Path filePath;

    private final GsonConfigurationLoader loader;
    private BasicConfigurationNode dataNode;


    private final Map<String, Class<?>> classMap = new HashMap<>();
    private final Map<Class<?>, Object> data = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> providers = new HashMap<>();

    private static GsonConfigurationLoader getLoader(Path path) {
        return GsonConfigurationLoader
                .builder()
                .path(path)
                .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                .build();
    }

    public GsonDataManager(Path filePath) {
        this.filePath = filePath;
        loader = getLoader(getDataPath());
    }

    public Path getDataPath() {
        return filePath;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clazz) {
        if (this.data.containsKey(clazz)) {
            return (T) this.data.get(clazz);
        }

        if (this.providers.containsKey(clazz)) {
            final T result = (T) this.providers.get(clazz).get();
            this.data.put(clazz, result);
            return result;
        }

        throw new IllegalArgumentException(clazz.getSimpleName() + " does not exist");
    }

    public void save() {
        for (var entry : classMap.entrySet()) {
            try {
                dataNode.node(entry.getKey()).set(data.get(entry.getValue()));
            } catch (ConfigurateException e) {
                Solstice.LOGGER.error("Could not save file {} data for {}. Skipping", this.filePath, entry.getKey(), e);
            }
        }
        try {
            loader.save(dataNode);
        } catch (ConfigurateException e) {
            Solstice.LOGGER.error("Could not save file {} data", this.filePath, e);
        }
    }

    public <T> void registerData(String id, Class<T> clazz, Supplier<T> creator) {
        classMap.put(id, clazz);
        providers.put(clazz, creator);
    }

    public void loadData(boolean force) throws ConfigurateException {
        if (dataNode == null || force) {
            dataNode = loader.load();
        }
        data.clear();
        for (var entry : classMap.entrySet()) {
            try {
                data.put(entry.getValue(), get(dataNode.node(entry.getKey()), entry.getValue()));
            } catch (Exception e) {
                Solstice.LOGGER.error("Could not load file {} data for {}. Using default values.", this.filePath, entry.getKey(), e);
                this.data.put(entry.getValue(), dataNode.node(entry.getKey()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T get(final BasicConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
        return node.get(TypeToken.get(clazz), (Supplier<T>) () -> (T) this.providers.get(clazz).get());
    }

    @SuppressWarnings("unchecked")
    private <T> void set(final BasicConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
        node.set(TypeToken.get(clazz), (T) this.providers.get(clazz).get());
    }

    public void prepareData() throws ConfigurateException {
        var node = loader.load();
        var defaults = loader.createNode();

        for (var map : classMap.entrySet()) {
            set(defaults.node(map.getKey()), map.getValue());
        }

        node.mergeFrom(defaults);
        loader.save(node);
        this.dataNode = node;
        loadData(false);
    }
}
