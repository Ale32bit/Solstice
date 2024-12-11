package me.alexdevs.solstice.modules.moderation;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.moderation.commands.*;
import me.alexdevs.solstice.modules.moderation.data.ModerationLocale;
import me.alexdevs.solstice.modules.moderation.data.ModerationPlayerData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class ModerationModule {
    public static final String ID = "moderation";
    public static final String IGNORE_BYPASS_PERMISSION = "solstice.ignore.bypass";

    private final Locale locale = Solstice.localeManager.getLocale(ModerationModule.ID);

    public ModerationModule() {
        Solstice.playerData.registerData(ID, ModerationPlayerData.class, ModerationPlayerData::new);
        Solstice.localeManager.registerModule(ID, ModerationLocale.MODULE);

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

    public static boolean isIgnoring(ServerPlayerEntity player, ServerPlayerEntity target) {
        return getPlayerData(player.getUuid()).ignoredPlayers.contains(target.getUuid()) && !Permissions.check(target, IGNORE_BYPASS_PERMISSION, 2);
    }
}
