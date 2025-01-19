package me.alexdevs.solstice.data;

import com.google.gson.*;
import me.alexdevs.solstice.Solstice;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerData {
    protected final UUID uuid;
    protected final Path filePath;
    protected final Path basePath;

    protected final Map<String, Class<?>> classMap = new HashMap<>();
    protected final Map<Class<?>, Object> data = new HashMap<>();
    protected final Map<Class<?>, Supplier<?>> providers = new HashMap<>();
    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .serializeNulls()
            .create();
    protected JsonObject node;

    public PlayerData(Path basePath, UUID uuid, Map<String, Class<?>> classMap, Map<Class<?>, Supplier<?>> providers) {
        this.uuid = uuid;
        this.classMap.putAll(classMap);
        this.providers.putAll(providers);
        this.basePath = basePath;
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

        var parentDir = filePath.getParent();
        var fileName = filePath.getFileName().toString();

        if(parentDir.toFile().mkdirs()) {
            Solstice.LOGGER.debug("Players data directory created.");
        }

        try {
            var temp = File.createTempFile(uuid.toString() + "-", ".json", parentDir.toFile());
            var tempWriter = new FileWriter(temp);
            gson.toJson(node, tempWriter);
            tempWriter.close();

            var target = filePath;
            var backup = parentDir.resolve(fileName + "_old");
            Util.backupAndReplace(target, temp.toPath(), backup);
        } catch (Exception e) {
            Solstice.LOGGER.error("Could not save {}. This will lead to data loss!", filePath, e);
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
        if (!this.filePath.toFile().exists())
            return new JsonObject();
        try (var fr = new FileReader(this.filePath.toFile())) {
            var reader = gson.newJsonReader(fr);
            var parser= JsonParser.parseReader(reader);
            var jsonObject = parser.getAsJsonObject();
            return jsonObject;
        } catch(IllegalStateException e) {
            Solstice.LOGGER.error("Could not load JSON node?", e);
            return null;
        } catch (IOException e) {
            Solstice.LOGGER.error("Could not load player data of UUID {}!", uuid, e);
            safeMove();
            return new JsonObject();
        }
    }

    protected void safeMove() {
        var df = new SimpleDateFormat("yyyyMMddHHmmss");
        var date = df.format(new Date());
        var newPath = basePath.resolve(String.format("%s.%s.json", uuid, date));
        if (filePath.toFile().renameTo(newPath.toFile())) {
            Solstice.LOGGER.warn("{} has been renamed to {}!", filePath, newPath);
        } else {
            Solstice.LOGGER.error("Could not move file {}. Solstice cannot safely manage player data.", filePath);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(@Nullable JsonElement node, Class<T> clazz) {
        if (node == null)
            return (T) providers.get(clazz).get();
        return gson.fromJson(node, clazz);
    }
}
