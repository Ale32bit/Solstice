package me.alexdevs.solstice.modules.jail.data;

import me.alexdevs.solstice.api.ServerLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;

public class JailPlayerData {
    public boolean jailed = false;
    public @Nullable String jailName = null;
    public @Nullable UUID jailedBy = null;
    public @Nullable Date jailedOn = null;
    public int jailTime = 0;
    public @Nullable String jailReason = null;
    public @Nullable ServerLocation previousLocation = null;
    public boolean teleportToPreviousLocation = false;
}
