package me.alexdevs.solstice.core.coreModule.data;

import me.alexdevs.solstice.api.ServerLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class CorePlayerData {
    public String username;
    public Date firstJoinedDate;
    public Date lastSeenDate;
    public String ipAddress;
    public @Nullable ServerLocation logoffPosition = null;
}
