package me.alexdevs.solstice.data;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerData {
    protected final UUID uuid;
    protected final Path filePath;

    protected final Map<String, Class<?>> classMap = new HashMap<>();
    protected final Map<Class<?>, Object> data = new HashMap<>();
    protected final Map<Class<?>, Supplier<?>> providers = new HashMap<>();

    protected JsonObject node;

    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .serializeNulls()
            .create();

    public PlayerData(Path basePath, UUID uuid, Map<String, Class<?>> classMap, Map<Class<?>, Supplier<?>> providers) {
        this.uuid = uuid;
        this.classMap.putAll(classMap);
        this.providers.putAll(providers);
        this.filePath = basePath.resolve(uuid + ".json");

        loadData(false);
    }

    public Path getDataPath() {
        return this.filePath;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clazz) {
        if (this.data.containsKey(clazz))
            return (T) this.data.get(clazz);

        if (this.providers.containsKey(clazz)) {
            final T result = (T) this.providers.get(clazz).get();
            this.data.put(clazz, result);
            return result;
        }

        throw new IllegalArgumentException(clazz.getSimpleName() + " does not exist");
    }

    public void save() {
        for (var entry : classMap.entrySet()) {
            var obj = data.get(entry.getValue());
            node.add(entry.getKey(), gson.toJsonTree(obj));
        }

        try (var fw = new FileWriter(this.filePath.toFile())) {
            gson.toJson(node, fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void registerData(String id, Class<T> clazz, Supplier<T> creator) {
        classMap.put(id, clazz);
        providers.put(clazz, creator);
    }

    public void loadData(boolean force) {
        if (node == null || force) {
            node = loadNode();
        }
        data.clear();

        for (var entry : classMap.entrySet()) {
            data.put(entry.getValue(), get(node.get(entry.getKey()), entry.getValue()));
        }
    }

    protected JsonObject loadNode() {
        if(!this.filePath.toFile().exists())
            return new JsonObject();
        try (var fr = new FileReader(this.filePath.toFile())) {
            var reader = gson.newJsonReader(fr);
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(@Nullable JsonElement node, Class<T> clazz) {
        if (node == null)
            return (T) providers.get(clazz).get();
        return gson.fromJson(node, clazz);
    }
}
