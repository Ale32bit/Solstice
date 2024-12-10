package me.alexdevs.solstice.modules.core.data;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CoreServerData {
    public ConcurrentHashMap<UUID, String> usernameCache = new ConcurrentHashMap<>();
}
