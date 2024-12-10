package me.alexdevs.solstice.modules.moderation;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.moderation.commands.*;
import me.alexdevs.solstice.modules.moderation.data.ModerationPlayerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

import java.util.UUID;

public class ModerationModule {
    public static final String ID = "moderation";

    private final Locale locale = Solstice.newLocaleManager.getLocale(ModerationModule.ID);

    public ModerationModule() {
        Solstice.playerData.registerData(ID, ModerationPlayerData.class, ModerationPlayerData::new);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new BanCommand(dispatcher, registryAccess, environment);
            new TempBanCommand(dispatcher, registryAccess, environment);
            new UnbanCommand(dispatcher, registryAccess, environment);
            new KickCommand(dispatcher, registryAccess, environment);
            new IgnoreCommand(dispatcher, registryAccess, environment);
            new IgnoreListCommand(dispatcher, registryAccess, environment);
            new MuteCommand(dispatcher, registryAccess, environment);
            new UnmuteCommand(dispatcher, registryAccess, environment);
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, parameters) -> {
            if (isMuted(player.getUuid())) {
                player.sendMessage(locale.get("youAreMuted"));
                return false;
            }
            return true;
        });
    }

    public static boolean isMuted(UUID playerUuid) {
        return getPlayerData(playerUuid).muted;
    }

    public static ModerationPlayerData getPlayerData(UUID playerUuid) {
        return Solstice.playerData.get(playerUuid).getData(ModerationPlayerData.class);
    }
}
