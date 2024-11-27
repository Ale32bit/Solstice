package me.alexdevs.solstice.data;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.ServerPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StateManager {
    private static final Charset charset = StandardCharsets.UTF_8;
    private ServerState serverState;
    private final ConcurrentHashMap<UUID, PlayerState> playerStates = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX") // iso 8601 my beloved
            .create();

    private Path basePath;
    private Path playersPath;
    private Path serverDataPath;

    public StateManager() {
    }

    public void register(Path path) {
        basePath = path;
        playersPath = basePath.resolve("players");
        serverDataPath = basePath.resolve("data.json");

        if (!basePath.toFile().exists()) {
            if (!basePath.toFile().mkdirs()) {
                Solstice.LOGGER.error("Could not create directory: {}", basePath.toAbsolutePath());
            }
        }

        if (!playersPath.toFile().exists()) {
            if (!playersPath.toFile().mkdirs()) {
                Solstice.LOGGER.error("Could not create directory: {}", playersPath.toAbsolutePath());
            }
        }

        if (serverDataPath.toFile().exists()) {
            try (var br = new BufferedReader(new FileReader(serverDataPath.toFile(), charset))) {
                serverState = gson.fromJson(br, ServerState.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            serverState = new ServerState();
            serverState.dirty = true;
        }

        ServerLifecycleEvents.SERVER_STOPPING.register(serverState -> save());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var playerState = loadPlayerState(handler.player.getUuid());
            var serverState = getServerState();

            serverState.usernameCache.put(player.getUuid(), player.getGameProfile().getName());

            playerState.username = player.getGameProfile().getName();
            playerState.lastSeenDate = new Date();
            playerState.ipAddress = handler.getPlayer().getIp();

            if (playerState.firstJoinedDate == null) {
                Solstice.LOGGER.info("Player {} joined for the first time!", player.getGameProfile().getName());
                playerState.firstJoinedDate = new Date();
                SolsticeEvents.WELCOME.invoker().onWelcome(player, server);
                var spawnPosition = serverState.spawn;

                if (spawnPosition != null) {
                    spawnPosition.teleport(player, false);
                }
            }

            if (playerState.username != null && !playerState.username.equals(player.getGameProfile().getName())) {
                Solstice.LOGGER.info("Player {} has changed their username from {}", player.getGameProfile().getName(), playerState.username);
                SolsticeEvents.USERNAME_CHANGE.invoker().onUsernameChange(player, playerState.username);
            }

            savePlayerState(player.getUuid(), playerState);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            var playerState = getPlayerState(handler.getPlayer().getUuid());
            playerState.logoffPosition = new ServerPosition(handler.getPlayer());
            playerState.lastSeenDate = new Date();
            savePlayerState(handler.getPlayer().getUuid(), playerState);
            playerStates.remove(handler.player.getUuid());
        });

    }

    public void saveServerState() {
        if (!serverState.dirty)
            return;

        if (serverState.saving)
            return;
        serverState.saving = true;

        //serverDataPath.toFile().renameTo(serverDataPath.getFileName().to);

        var json = gson.toJson(serverState, ServerState.class);
        try (var fw = new FileWriter(serverDataPath.toFile(), charset)) {
            fw.write(json);
            serverState.dirty = false;
        } catch (IOException e) {
            Solstice.LOGGER.error("Could not save server state", e);
        }
        serverState.saving = false;
    }

    public void savePlayerState(UUID uuid, PlayerState playerState) {
        if (!playerState.dirty)
            return;

        if (playerState.saving)
            return;
        playerState.saving = true;

        var path = playersPath.resolve(uuid.toString() + ".json");
        var json = gson.toJson(playerState, PlayerState.class);
        try (var fw = new FileWriter(path.toFile(), charset)) {
            fw.write(json);
            playerState.dirty = false;
        } catch (IOException e) {
            Solstice.LOGGER.error("Could not save player state", e);
        }
        playerState.saving = false;
    }

    public void save() {
        saveServerState();
        playerStates.forEach(this::savePlayerState);
    }

    private PlayerState loadPlayerState(UUID uuid) {
        var path = playersPath.resolve(uuid.toString() + ".json");
        PlayerState playerState;
        if (path.toFile().exists()) {
            try (var br = new BufferedReader(new FileReader(path.toFile(), charset))) {
                playerState = gson.fromJson(br, PlayerState.class);
            } catch (Exception e) {
                Solstice.LOGGER.error("Could not load player state: " + path.toAbsolutePath(), e);
                return null;
            }
        } else {
            playerState = new PlayerState();
            playerState.dirty = true;
        }

        playerStates.put(uuid, playerState);
        playerState.uuid = uuid;

        return playerState;
    }

    public PlayerState getPlayerState(UUID uuid) {
        PlayerState playerState;
        if (playerStates.containsKey(uuid)) {
            playerState = playerStates.get(uuid);
        } else {
            playerState = loadPlayerState(uuid);
        }

        if (playerState == null)
            return null;

        playerState.dirty = true;
        return playerState;
    }

    public ServerState getServerState() {
        serverState.dirty = true;
        return serverState;
    }
}
