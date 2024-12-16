package me.alexdevs.solstice.modules.kit;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.kit.data.KitPlayerData;
import me.alexdevs.solstice.modules.kit.data.KitServerData;

public class KitModule {
    public static final String ID = "kit";

    public KitModule() {
        Solstice.playerData.registerData(ID, KitPlayerData.class, KitPlayerData::new);
        Solstice.serverData.registerData(ID, KitServerData.class, KitServerData::new);
    }
}
