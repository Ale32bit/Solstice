package me.alexdevs.solstice.modules.ignore;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.AllowP2PMessageCallback;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.ignore.commands.IgnoreCommand;
import me.alexdevs.solstice.modules.ignore.commands.IgnoreListCommand;
import me.alexdevs.solstice.modules.ignore.data.IgnoreLocale;
import me.alexdevs.solstice.modules.ignore.data.IgnorePlayerData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
public class IgnoreModule extends ModuleBase.Toggleable {


    public IgnoreModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(IgnoreLocale.MODULE);
        registerPlayerData(IgnorePlayerData.class, IgnorePlayerData::new);

        commands.add(new IgnoreCommand(this));
        commands.add(new IgnoreListCommand(this));

        AllowP2PMessageCallback.ALLOW_MESSAGE.register((player, receiver, message) -> !isIgnoring(receiver, player));
    }

    public IgnorePlayerData getPlayerData(UUID playerUuid) {
        return Solstice.playerData.get(playerUuid).getData(IgnorePlayerData.class);
    }

    public boolean isIgnoring(ServerPlayer player, ServerPlayer target) {
        if(!isEnabled())
            return false;

        return getPlayerData(player.getUUID()).ignoredPlayers.contains(target.getUUID()) && !Permissions.check(target, this.getPermissionNode("exempt"), 2);
    }
}
