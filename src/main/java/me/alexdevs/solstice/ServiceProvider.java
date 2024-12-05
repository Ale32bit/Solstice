package me.alexdevs.solstice;

import me.alexdevs.solstice.data.PlayerDataManager;
import me.alexdevs.solstice.data.ServerDataManager;

public class ServiceProvider {
    private final Solstice solstice;

    private final PlayerDataManager playerDataManager;
    private final ServerDataManager serverDataManager;

    public ServiceProvider(Solstice solstice) {
        this.solstice = solstice;

        this.playerDataManager = new PlayerDataManager();
        this.serverDataManager = new ServerDataManager();
    }
}
