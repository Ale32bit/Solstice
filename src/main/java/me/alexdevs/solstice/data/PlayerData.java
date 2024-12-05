package me.alexdevs.solstice.data;

import io.leangen.geantyref.TypeToken;
import me.alexdevs.solstice.Solstice;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerData {
    private final Path basePath;
    private final UUID uuid;

    private final HoconConfigurationLoader loader;
    private CommentedConfigurationNode dataNode;


    private final Map<String, Class<?>> classMap;
    private final Map<Class<?>, Object> data = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> providers;

    private static HoconConfigurationLoader getLoader(Path path) {
        return HoconConfigurationLoader
                .builder()
                .path(path)
                .defaultOptions(opts -> opts.shouldCopyDefaults(true))
                .build();
    }

    public PlayerData(Path basePath, UUID uuid, Map<String, Class<?>> classMap, Map<Class<?>, Supplier<?>> providers) {
        this.basePath = basePath;
        this.uuid = uuid;
        this.classMap = classMap;
        this.providers = providers;

        loader = getLoader(getDataPath());
    }

    public Path getDataPath() {
        return basePath.resolve(uuid + ".dat");
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clazz) {
        if(this.data.containsKey(clazz)) {
            return (T) this.data.get(clazz);
        }

        if(this.providers.containsKey(clazz)) {
            final T result = (T) this.providers.get(clazz).get();
            this.data.put(clazz, result);
            return result;
        }

        throw new IllegalArgumentException(clazz.getSimpleName() + " does not exist");
    }

    public void save() {
        for(var entry : classMap.entrySet()) {
            try {
                dataNode.node(entry.getKey()).set(data.get(entry.getValue()));
            } catch (ConfigurateException e) {
                Solstice.LOGGER.error("Could not save player data for {}. Skipping", entry.getKey(), e);
            }
        }
        try {
            loader.save(dataNode);
        } catch (ConfigurateException e) {
            Solstice.LOGGER.error("Could not save player data to file!", e);
        }
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
                Solstice.LOGGER.error("Could not load player data for {}. Using default values.", entry.getKey(), e);
                this.data.put(entry.getValue(), dataNode.node(entry.getKey()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T get(final CommentedConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
        return node.get(TypeToken.get(clazz), (Supplier<T>) () -> (T) this.providers.get(clazz).get());
    }

    @SuppressWarnings("unchecked")
    private <T> void set(final CommentedConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
        node.set(TypeToken.get(clazz), (T) this.providers.get(clazz).get());
    }

    public void prepareData() throws ConfigurateException {
        var playerNode = loader.load();
        var defaults = loader.createNode();

        for (var map : classMap.entrySet()) {
            set(defaults.node(map.getKey()), map.getValue());
        }

        playerNode.mergeFrom(defaults);
        loader.save(playerNode);
        this.dataNode = playerNode;
        loadData(false);
    }
}
