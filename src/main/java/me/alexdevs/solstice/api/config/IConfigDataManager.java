package me.alexdevs.solstice.api.config;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface IConfigDataManager {
    /**
     * Register a config section.
     * @param id ID of the section with namespace. This uses the ID of the mod as the namespace and the section ID as the path.
     * @param classOfConfig Class of the config itself. Remember to include the annotation @{@link ConfigSerializable}
     * @param creator Supplier of the config that contains the default values when creating the initial data.
     * @param <T> Class of the config.
     */
    <T> void registerData(ResourceLocation id, Class<T> classOfConfig, Supplier<T> creator);

    /**
     * Get the object of the configuration data.
     * @param classOfConfig Class of the config.
     * @return Configuration object
     * @param <T> Class of the config.
     */
    <T> T getData(Class<T> classOfConfig);

    /**
     * Save the config file to disk.
     */
    void save();

    /**
     * Load the config file from disk, overwriting the loaded config.
     */
    void load() throws ConfigurateException;

    /**
     * Get the path of the configuration file.
     * @return Path of the configuration file.
     */
    Path getPath();
}
