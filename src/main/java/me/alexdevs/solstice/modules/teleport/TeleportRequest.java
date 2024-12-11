package me.alexdevs.solstice.modules.teleport;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.teleport.data.TeleportConfig;

import java.util.UUID;

public class TeleportRequest {
    public UUID requestId = UUID.randomUUID();
    public UUID player;
    public UUID target;
    public int remainingTicks;

    public TeleportRequest(UUID player, UUID target) {
        this.player = player;
        this.target = target;
        // Seconds in config per 20 ticks
        this.remainingTicks = Solstice.configManager.getData(TeleportConfig.class).teleportRequestTimeout * 20;
    }

    public void expire() {
        remainingTicks = 0;
    }
}
