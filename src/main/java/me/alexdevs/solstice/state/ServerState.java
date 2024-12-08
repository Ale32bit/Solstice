package me.alexdevs.solstice.state;

import me.alexdevs.solstice.api.ServerPosition;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerState {
    boolean dirty = false;
    boolean saving = false;

    @Expose
    public @Nullable ServerPosition spawn;
    @Expose
    public ConcurrentHashMap<String, ServerPosition> warps = new ConcurrentHashMap<>();
    @Expose
    public ConcurrentHashMap<UUID, String> usernameCache = new ConcurrentHashMap<>();

}
