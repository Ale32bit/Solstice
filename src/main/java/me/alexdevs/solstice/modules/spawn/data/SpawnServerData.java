package me.alexdevs.solstice.modules.spawn.data;

import me.alexdevs.solstice.api.ServerLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SpawnServerData {
    @Deprecated
    public @Nullable ServerLocation spawn;

    public @Nullable ServerLocation firstSpawn;

    public Map<String, ServerLocation> spawnPoints = new HashMap<>();
}
