package me.alexdevs.solstice.data;

import com.google.gson.*;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
//? < 1.21.11
import net.minecraft.Util;
//? >= 1.21.11
//import net.minecraft.util.Util;
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
import java.util.function.Supplier;

public class ServerData {
    protected final Map<SolsticeIdentifier, Class<?>> classMap = new HashMap<>();
    protected final Map<Class<?>, Object> data = new HashMap<>();
    protected final Map<Class<?>, Supplier<?>> providers = new HashMap<>();
    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .serializeNulls()
            .create();
    protected Path filePath;
    protected JsonObject node;

    public Path getDataPath() {
        return this.filePath;
    }

    public void setDataPath(Path filePath) {
        this.filePath = filePath;
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
            node.add(entry.getKey().toString(), gson.toJsonTree(obj));
        }

        var parentDir = filePath.getParent();
        var fileName = filePath.getFileName().toString();

        try {
            var temp = File.createTempFile("server-", ".json", parentDir.toFile());
            var tempWriter = new FileWriter(temp);
            gson.toJson(node, tempWriter);
            tempWriter.close();

            var target = filePath;
            var backup = parentDir.resolve(fileName + "_old");
            Util.safeReplaceFile(target, temp.toPath(), backup);
        } catch (Exception e) {
            Solstice.LOGGER.error("Could not save {}. This will lead to data loss!", filePath, e);
        }
    }

    public <T> void registerData(SolsticeIdentifier id, Class<T> clazz, Supplier<T> creator) {
        classMap.put(id, clazz);
        providers.put(clazz, creator);
    }

    public void loadData(boolean force) {
        if (node == null || force) {
            node = loadNode();
        }
        data.clear();

        var migrated = false;
        for (var entry : classMap.entrySet()) {
            var key = entry.getKey().toString();
            var val = node.get(key);
            if (val == null) {
                var legacyKey = entry.getKey().getPath();
                val = node.get(legacyKey);
                node.remove(legacyKey);
                node.add(key, val);
                migrated = true;
            }
            data.put(entry.getValue(), get(val, entry.getValue()));
        }

        if (migrated) {
            backup();
        }
    }

    protected JsonObject loadNode() {
        if (!this.filePath.toFile().exists())
            return new JsonObject();

        try (var fr = new FileReader(this.filePath.toFile())) {
            var reader = gson.newJsonReader(fr);
            return JsonParser.parseReader(reader).getAsJsonObject();

        } catch (IOException e) {
            Solstice.LOGGER.error("Could not load server data!", e);
            safeMove();
            return new JsonObject();
        }
    }

    protected void safeMove() {
        var df = new SimpleDateFormat("yyyyMMddHHmmss");
        var date = df.format(new Date());
        var basePath = filePath.getParent();
        var newPath = basePath.resolve(String.format("server.%s.json", date));
        if (filePath.toFile().renameTo(newPath.toFile())) {
            Solstice.LOGGER.warn("{} has been renamed to {}!", filePath, newPath);
        } else {
            Solstice.LOGGER.error("Could not move file {}. Solstice cannot safely manage server data.", filePath);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T get(@Nullable JsonElement node, Class<T> clazz) {
        if (node == null)
            return (T) providers.get(clazz).get();
        return gson.fromJson(node, clazz);
    }

    protected void backup() {
        var path = getDataPath();
        var parentDir = path.getParent();
        var fileName = path.getFileName();
        var backup = parentDir.resolve(fileName.toString() + "_backup");
        if(path.toFile().renameTo(backup.toFile())) {
            Solstice.LOGGER.warn("The server data file has been migrated and the original {} has been renamed to {}!", path, backup);
        } else {
            Solstice.LOGGER.error("Could not create backup of server data file!");
        }
    }
}
