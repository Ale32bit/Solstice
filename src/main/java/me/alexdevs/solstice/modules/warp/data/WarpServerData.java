package me.alexdevs.solstice.modules.warp.data;

import me.alexdevs.solstice.api.ServerPosition;

import java.util.concurrent.ConcurrentHashMap;

public class WarpServerData {
    public ConcurrentHashMap<String, ServerPosition> warps = new ConcurrentHashMap<>();
}
