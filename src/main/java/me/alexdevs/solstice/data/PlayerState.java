package me.alexdevs.solstice.data;

import me.alexdevs.solstice.api.PlayerMail;
import me.alexdevs.solstice.api.ServerPosition;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerState {
    boolean dirty = false;
    boolean saving = false;

    @Expose
    public UUID uuid;
    @Expose
    public String username;
    @Expose
    public @Nullable String nickname;
    @Expose
    public @Nullable Date firstJoinedDate;
    @Expose
    public @Nullable Date lastSeenDate;
    @Expose
    public @Nullable String ipAddress;
    @Expose
    public @Nullable ServerPosition logoffPosition = null;
    @Expose
    public int activeTime = 0;
    @Expose
    public ConcurrentHashMap<String, ServerPosition> homes = new ConcurrentHashMap<>();
    @Expose
    public ArrayList<PlayerMail> mails = new ArrayList<>();
    @Expose
    public boolean muted = false;
    @Expose
    public ArrayList<UUID> ignoredPlayers = new ArrayList<>();
}
