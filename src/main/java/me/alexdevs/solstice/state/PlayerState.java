package me.alexdevs.solstice.state;

import me.alexdevs.solstice.api.ServerPosition;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PlayerState {
    boolean dirty = false;
    boolean saving = false;

    @Expose
    public UUID uuid;
    @Expose
    public String username;
    @Expose
    public @Nullable Date firstJoinedDate;
    @Expose
    public @Nullable Date lastSeenDate;
    @Expose
    public @Nullable String ipAddress;
    @Expose
    public @Nullable ServerPosition logoffPosition = null;
    @Expose
    public ArrayList<UUID> ignoredPlayers = new ArrayList<>();
}
