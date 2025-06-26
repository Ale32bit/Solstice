package me.alexdevs.solstice.modules.ignore;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ignore.commands.IgnoreCommand;
import me.alexdevs.solstice.modules.ignore.commands.IgnoreListCommand;
import me.alexdevs.solstice.modules.ignore.data.IgnoreLocale;
import me.alexdevs.solstice.modules.ignore.data.IgnorePlayerData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

public class IgnoreModule extends ModuleBase.Toggleable {
    

    public IgnoreModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(IgnoreLocale.MODULE);
        registerPlayerData(IgnorePlayerData.class, IgnorePlayerData::new);

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

    public boolean isIgnoring(ServerPlayer player, ServerPlayer target) {
        return getPlayerData(player.getUUID()).ignoredPlayers.contains(target.getUUID()) && !Permissions.check(target, this.getPermissionNode("exempt"), 2);
    }
}
