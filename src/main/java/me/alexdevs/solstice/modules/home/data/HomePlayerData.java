package me.alexdevs.solstice.modules.home.data;

import me.alexdevs.solstice.api.ServerPosition;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.concurrent.ConcurrentHashMap;

@ConfigSerializable
public class HomePlayerData {
    public ConcurrentHashMap<String, ServerPosition> homes = new ConcurrentHashMap<>();
}
