package me.alexdevs.solstice.modules.core.data;

import me.alexdevs.solstice.api.ServerPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class CorePlayerData {
    public String username;
    public Date firstJoinedDate;
    public Date lastSeenDate;
    public String ipAddress;
    public @Nullable ServerPosition logoffPosition = null;
}
