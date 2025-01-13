package me.alexdevs.solstice.modules.jail.data;

import me.alexdevs.solstice.api.ServerLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JailServerData {
    public Map<String, ServerLocation> jails = new ConcurrentHashMap<>();
}
