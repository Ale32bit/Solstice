package me.alexdevs.solstice.modules.ignore;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ignore.commands.IgnoreCommand;
import me.alexdevs.solstice.modules.ignore.commands.IgnoreListCommand;
import me.alexdevs.solstice.modules.ignore.data.IgnoreLocale;
import me.alexdevs.solstice.modules.ignore.data.IgnorePlayerData;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class IgnoreModule extends ModuleBase.Toggleable {
    public static final String ID = "ignore";

    public IgnoreModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, IgnoreLocale.MODULE);
        Solstice.playerData.registerData(ID, IgnorePlayerData.class, IgnorePlayerData::new);

        commands.add(new IgnoreCommand(this));
        commands.add(new IgnoreListCommand(this));
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Solstice.modules.getModule(StylingModule.class).isEnabled();
    }

    public IgnorePlayerData getPlayerData(UUID playerUuid) {
        return Solstice.playerData.get(playerUuid).getData(IgnorePlayerData.class);
    }

    public boolean isIgnoring(ServerPlayerEntity player, ServerPlayerEntity target) {
        return getPlayerData(player.getUuid()).ignoredPlayers.contains(target.getUuid()) && !Permissions.check(target, this.getPermissionNode("exempt"), 2);
    }
}
