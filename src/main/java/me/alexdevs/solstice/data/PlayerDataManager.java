package me.alexdevs.solstice.data;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import net.minecraft.server.network.ServerPlayerEntity;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerDataManager {
    private final Map<String, Class<?>> classMap = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> providers = new HashMap<>();
    private final Map<UUID, PlayerData> playerData = new HashMap<>();
    private Path basePath;

    public Path getDataPath() {
        return basePath;
    }

    public void setDataPath(Path basePath) {
        this.basePath = basePath;
    }

    /**
     * Register data model for the player
     *
     * @param id      Module key in the data
     * @param clazz   Class of data
     * @param creator Default values provider
     * @param <T>     Type of class of data
     */
    public <T> void registerData(String id, Class<T> clazz, Supplier<T> creator) {
        classMap.put(id, clazz);
        providers.put(clazz, creator);
    }

    /**
     * Get data of a player. Will load if not loaded.
     *
     * @param uuid Player UUID
     * @return player data
     */
    public PlayerData get(UUID uuid) {
        if (!playerData.containsKey(uuid)) {
            return load(uuid);
        }
        return playerData.get(uuid);
    }

    /**
     * Get data of a player. Will load if not loaded.
     *
     * @param player Player
     * @return player data
     */
    public PlayerData get(ServerPlayerEntity player) {
        return get(player.getUuid());
    }

    /**
     * Get data of a player. Will load if not loaded.
     *
     * @param profile Player profile
     * @return player data
     */
    public PlayerData get(GameProfile profile) {
        return get(profile.getId());
    }

    /**
     * Save player data and unload from memory
     *
     * @param uuid Player UUID
     */
    public void dispose(UUID uuid) {
        if (playerData.containsKey(uuid)) {
            Solstice.LOGGER.info("Unloading player data {}", uuid);
            var data = playerData.remove(uuid);
            data.save();
        }
    }

    public void disposeMissing(List<UUID> uuids) {
        for(var entry : playerData.entrySet()) {
            if(!uuids.contains(entry.getKey())) {
                dispose(entry.getKey());
            }
        }
    }

    private PlayerData load(UUID uuid) {
        Solstice.LOGGER.info("Loading player data {}", uuid);
        var data = new PlayerData(this.basePath, uuid, classMap, providers);
        playerData.put(uuid, data);
        return data;
    }

    /**
     * Save all player data without disposing.
     */
    public void saveAll() {
        if (!this.basePath.toFile().exists()) {
            this.basePath.toFile().mkdirs();
        }
        for (var entry : playerData.entrySet()) {
            var data = entry.getValue();
            data.save();
        }
    }
}
