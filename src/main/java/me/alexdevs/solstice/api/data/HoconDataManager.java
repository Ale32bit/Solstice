package me.alexdevs.solstice.api.data;

import io.leangen.geantyref.TypeToken;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.data.serializers.DateSerializer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HoconDataManager {
    protected final Map<ResourceLocation, Class<?>> classMap = new HashMap<>();
    protected final Map<Class<?>, Object> data = new HashMap<>();
    protected final Map<Class<?>, Supplier<?>> providers = new HashMap<>();
    protected Path filePath;
    protected HoconConfigurationLoader loader;
    protected CommentedConfigurationNode dataNode;

    public HoconDataManager() {

    }

    public HoconDataManager(final Path filePath) {
        setDataPath(filePath);
    }

    protected static HoconConfigurationLoader getLoader(Path path) {
        return HoconConfigurationLoader
                .builder()
                .path(path)
                .defaultOptions(opts -> opts
                        .shouldCopyDefaults(true)
                        .serializers(TypeSerializerCollection.defaults()
                                .childBuilder()
                                .registerExact(DateSerializer.TYPE)
                                .build()))
                .build();
    }

    public Path getDataPath() {
        return filePath;
    }

    public void setDataPath(Path filePath) {
        this.filePath = filePath;
        loader = getLoader(getDataPath());
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
                Solstice.LOGGER.error("Could not save server data for {}. Skipping", entry.getKey(), e);
            }
        }
        try {
            loader.save(dataNode);
        } catch (ConfigurateException e) {
            Solstice.LOGGER.error("Could not save server data to file!", e);
        }
    }

    public <T> void registerData(ResourceLocation id, Class<T> clazz, Supplier<T> creator) {
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
                var obj = dataNode.node(entry.getKey().toString());
                if (obj == null) {
                    obj = dataNode.node(entry.getKey().getPath());
                }
                data.put(entry.getValue(), get(obj, entry.getValue()));
            } catch (Exception e) {
                Solstice.LOGGER.error("Could not load server data for {}. Using default values.", entry.getKey(), e);
                this.data.put(entry.getValue(), dataNode.node(entry.getKey()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(final CommentedConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
        return node.get(TypeToken.get(clazz), (Supplier<T>) () -> (T) this.providers.get(clazz).get());
    }

    @SuppressWarnings("unchecked")
    protected <T> void set(final CommentedConfigurationNode node, final Class<T> clazz) throws ConfigurateException {
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
