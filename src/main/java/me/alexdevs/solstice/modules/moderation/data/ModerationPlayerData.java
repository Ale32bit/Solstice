package me.alexdevs.solstice.modules.moderation.data;

import java.util.ArrayList;
import java.util.UUID;

public class ModerationPlayerData {
    public boolean muted = false;
    public ArrayList<UUID> ignoredPlayers = new ArrayList<>();
    public ArrayList<String> warns = new ArrayList<>();
}
