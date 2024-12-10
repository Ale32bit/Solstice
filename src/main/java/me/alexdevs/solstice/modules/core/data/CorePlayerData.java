package me.alexdevs.solstice.modules.core.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Date;

public class CorePlayerData {
    public String username;
    public Date firstJoinedDate;
    public Date lastSeenDate;
    public String ipAddress;
}
