package me.alexdevs.solstice.modules.back.data;

import me.alexdevs.solstice.api.ServerLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class BackPlayerData {
    public @Nullable Date lastTeleportDate;
    public @Nullable ServerLocation lastTeleportLocation;
}
