package me.alexdevs.solstice.modules.mute;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.mute.commands.MuteCommand;
import me.alexdevs.solstice.modules.mute.commands.UnmuteCommand;
import me.alexdevs.solstice.modules.mute.data.MuteLocale;
import me.alexdevs.solstice.modules.mute.data.MutePlayerData;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

import java.util.UUID;

public class MuteModule extends ModuleBase.Toggleable {
    public static final String ID = "mute";

    public MuteModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, MuteLocale.MODULE);
        Solstice.playerData.registerData(ID, MutePlayerData.class, MutePlayerData::new);

        commands.add(new MuteCommand(this));
        commands.add(new UnmuteCommand(this));

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, parameters) -> {
            if (isMuted(player.getUUID())) {
                player.sendSystemMessage(locale().get("youAreMuted"));
                return false;
            }
            return true;
        });
    }

    public MutePlayerData getPlayerData(UUID playerUuid) {
        return Solstice.playerData.get(playerUuid).getData(MutePlayerData.class);
    }

    public boolean isMuted(UUID playerUuid) {
        return getPlayerData(playerUuid).muted;
    }


}
