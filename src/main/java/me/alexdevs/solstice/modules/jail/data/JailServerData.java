package me.alexdevs.solstice.modules.jail.data;

import me.alexdevs.solstice.api.ServerPosition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JailServerData {
    public Map<String, ServerPosition> jails = new ConcurrentHashMap<>();
}
