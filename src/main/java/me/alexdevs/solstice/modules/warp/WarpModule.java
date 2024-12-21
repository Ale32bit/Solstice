package me.alexdevs.solstice.modules.warp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.warp.commands.DeleteWarpCommand;
import me.alexdevs.solstice.modules.warp.commands.SetWarpCommand;
import me.alexdevs.solstice.modules.warp.commands.WarpCommand;
import me.alexdevs.solstice.modules.warp.commands.WarpsCommand;
import me.alexdevs.solstice.modules.warp.data.WarpLocale;
import me.alexdevs.solstice.modules.warp.data.WarpServerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.Collection;
import java.util.List;

public class WarpModule extends ModuleBase {
    public static final String ID = "warp";

    private final List<ModCommand<WarpModule>> commands = List.of(
            new WarpCommand(this),
            new SetWarpCommand(this),
            new DeleteWarpCommand(this),
            new WarpsCommand(this)
    );

    public WarpModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, WarpLocale.MODULE);
        Solstice.serverData.registerData(ID, WarpServerData.class, WarpServerData::new);
    }

    @Override
    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }
}
