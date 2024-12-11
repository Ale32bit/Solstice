package me.alexdevs.solstice.modules.warp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.warp.commands.DeleteWarpCommand;
import me.alexdevs.solstice.modules.warp.commands.SetWarpCommand;
import me.alexdevs.solstice.modules.warp.commands.WarpCommand;
import me.alexdevs.solstice.modules.warp.commands.WarpsCommand;
import me.alexdevs.solstice.modules.warp.data.WarpLocale;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class WarpModule {
    public static final String ID = "warp";

    public WarpModule() {
        Solstice.localeManager.registerModule(ID, WarpLocale.MODULE);
        Solstice.serverData.registerData(ID, WarpServerData.class, WarpServerData::new);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new WarpCommand(dispatcher, registryAccess, environment);
            new SetWarpCommand(dispatcher, registryAccess, environment);
            new DeleteWarpCommand(dispatcher, registryAccess, environment);
            new WarpsCommand(dispatcher, registryAccess, environment);
        });
    }
}
