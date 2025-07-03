package me.alexdevs.solstice.api.config;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.config.serializers.DateSerializer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Basically hocon data manager.
 * This one is specific for namespaces, perfect for config.
 */
public class ConfigDataManager implements IConfigDataManager {
    public static final int CONFIG_VERSION = 2;
    private final Map<ResourceLocation, Class<?>> classMap = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> defaultSuppliers = new HashMap<>();
    private final Map<Class<?>, Object> dataMap = new HashMap<>();

    private final Path filePath;
    private final HoconConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public ConfigDataManager(Path filePath) {
        this.filePath = filePath;
        loader = HoconConfigurationLoader.builder()
                .path(filePath)
                .emitComments(true)
                .prettyPrinting(true)
                .headerMode(HeaderMode.PRESET)
                .defaultOptions(opts -> opts
                        .shouldCopyDefaults(true)
                        .header("Solstice Configuration File. Consult https://solstice.alexdevs.me/ for documentation.")
                        .serializers(TypeSerializerCollection.defaults()
                                .childBuilder()
                                .registerExact(DateSerializer.TYPE)
                                .build()))
                .build();
    }

    @Override
    public <T> void registerData(ResourceLocation id, Class<T> classOfConfig, Supplier<T> creator) {
        if (classMap.containsKey(id) || defaultSuppliers.containsKey(classOfConfig)) {
            throw new IllegalArgumentException("Config with identifier " + id + " already registered");
        }

        classMap.put(id, classOfConfig);
        defaultSuppliers.put(classOfConfig, creator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getData(Class<T> classOfConfig) {
        // try to get the data, but, if it does not exist, create from the default supplier.
        if (!dataMap.containsKey(classOfConfig)) {
            if (defaultSuppliers.containsKey(classOfConfig)) {
                var creator = (Supplier<T>) defaultSuppliers.get(classOfConfig);
                var data = creator.get();
                dataMap.put(classOfConfig, data);
                return data;
            }
            throw new IllegalArgumentException("Config with class " + classOfConfig.getName() + " not registered");
        }

        var data = dataMap.get(classOfConfig);
        return (T) data;
    }

    private Map<String, List<Map.Entry<ResourceLocation, Class<?>>>> getSections() {
        return classMap.entrySet().stream()
                .collect(Collectors
                        .groupingBy(entry -> entry.getKey().getNamespace()));
    }

    @Override
    public void save() {
        var namespaces = getSections();

        for (var group : namespaces.entrySet()) {
            var namespace = group.getKey();
            var section = root.node(namespace);
            for (var entry : group.getValue()) {
                try {
                    var path = entry.getKey().getPath();
                    var classOfConfig = classMap.get(entry.getKey());
                    section.node(path).set(classOfConfig, dataMap.get(classOfConfig));
                } catch (ConfigurateException e) {
                    Solstice.LOGGER.error("Could not save configuration data for {}. Skipping", entry.getKey(), e);
                }
            }
        }

        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            Solstice.LOGGER.error("Could not save configuration data to file!", e);
        }
    }

    @Override
    public void load() throws ConfigurateException {
        root = loader.load();

        var version = root.node("version").getInt(1);
        root.removeChild("version"); // clear the version node, so migration does not mistakenly move it.
        if (version != CONFIG_VERSION) {
            Solstice.LOGGER.warn("Configuration version mismatch! Attempting migration...");
            migrate(version);
        }

        var defaults = loader.createNode();
        defaults.node("version")
                .comment("!! DO NOT CHANGE THIS VALUE !! Configuration version used for migrations.")
                .set(CONFIG_VERSION);

        var namespaces = getSections();

        // load default values and merge
        for (var group : namespaces.entrySet()) {
            var namespace = group.getKey();
            var section = defaults.node(namespace);
            for (var entry : group.getValue()) {
                var path = entry.getKey().getPath();
                var creator = defaultSuppliers.get(entry.getValue());
                section.node(path).set(creator.get());
            }
        }

        root.mergeFrom(defaults);

        // load data into dataMap

        for (var group : namespaces.entrySet()) {
            var namespace = group.getKey();
            var section = root.node(namespace);
            for (var entry : group.getValue()) {
                var path = entry.getKey().getPath();
                var configClass = entry.getValue();
                var obj = section.node(path).get(configClass);
                dataMap.put(configClass, obj);
            }
        }
    }

    @Override
    public Path getPath() {
        return this.filePath;
    }

    protected void migrate(int version) {
        if (version > CONFIG_VERSION) {
            throw new IllegalArgumentException("Configuration file has a version higher than the supported version!");
        }

        for(int i = version; i < CONFIG_VERSION; i++) {
            Solstice.LOGGER.info("Sequentially migrating configuration from version {} to version {}", i, i + 1);
            if (i == 1) {
                migrateV1();
            }
        }
    }

    private void migrateV1() {
        var newRoot = loader.createNode();

        // the first version does not use a namespace; therefore, we use Solstice's as default
        var defaultNamespace = Solstice.ID.getNamespace();
        var section = newRoot.node(defaultNamespace);

        for(var entry : root.childrenMap().entrySet()) {
            var path = entry.getKey().toString();
            var node = entry.getValue();
            section.node(path).mergeFrom(node);
        }

        root = newRoot;
    }
}
