package me.alexdevs.solstice.modules.mute.data;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class MutePlayerData {
    public boolean muted = false;
    public @Nullable Date mutedUntil = null;
}
