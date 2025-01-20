package me.alexdevs.solstice.modules.warp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.warp.commands.DeleteWarpCommand;
import me.alexdevs.solstice.modules.warp.commands.SetWarpCommand;
import me.alexdevs.solstice.modules.warp.commands.WarpCommand;
import me.alexdevs.solstice.modules.warp.commands.WarpsCommand;
import me.alexdevs.solstice.modules.warp.data.WarpLocale;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public class WarpModule extends ModuleBase.Toggleable {
    public static final String ID = "warp";

    public WarpModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, WarpLocale.MODULE);
        Solstice.serverData.registerData(ID, WarpServerData.class, WarpServerData::new);

        commands.add(new WarpCommand(this));
        commands.add(new WarpsCommand(this));
        commands.add(new SetWarpCommand(this));
        commands.add(new DeleteWarpCommand(this));
    }

    public boolean canUseWarp(ServerPlayerEntity player, String warpName) {
        return Permissions.check(player, getPermissionNode("warps." + warpName), true);
    }
}
