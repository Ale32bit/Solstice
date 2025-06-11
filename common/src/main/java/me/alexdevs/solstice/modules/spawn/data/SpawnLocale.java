package me.alexdevs.solstice.modules.spawn.data;

import java.util.Map;

public class SpawnLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("teleporting", "<gold>Teleporting to spawn...</gold>"),
            Map.entry("noWorldPermission", "<gold>You do not have permission to teleport to this world spawn.</gold>"),
            Map.entry("noFirstSpawn", "<gold>There is no first spawn yet.</gold>"),
            Map.entry("firstSpawnSet", "<green>First spawn set!</green>"),
            Map.entry("firstSpawnDeleted", "<gold>First spawn deleted!</gold>"),
            Map.entry("worldSpawnSet", "<green><yellow>${world}</yellow> spawn point set to <yellow>${coordinates}</yellow>.</green>")
    );
}
