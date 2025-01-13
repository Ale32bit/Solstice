package me.alexdevs.solstice.modules.warp.data;

import me.alexdevs.solstice.api.ServerLocation;

import java.util.concurrent.ConcurrentHashMap;

public class WarpServerData {
    public ConcurrentHashMap<String, ServerLocation> warps = new ConcurrentHashMap<>();
}
