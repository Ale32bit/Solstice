package me.alexdevs.solstice.core.coreModule.data;

import me.alexdevs.solstice.api.ServerLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class CorePlayerData {
    public String username;
    public @Nullable Date firstJoinedDate;
    public @Nullable Date lastSeenDate;
    public @Nullable String ipAddress;
    public @Nullable ServerLocation logoffPosition = null;
}
