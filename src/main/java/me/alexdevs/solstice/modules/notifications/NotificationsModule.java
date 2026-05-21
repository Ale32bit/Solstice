package me.alexdevs.solstice.modules.notifications;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.notifications.commands.NotificationsCommand;
import me.alexdevs.solstice.modules.notifications.data.NotificationsConfig;
import me.alexdevs.solstice.modules.notifications.data.NotificationsLocale;
import me.alexdevs.solstice.modules.notifications.data.NotificationsPlayerData;
import me.alexdevs.solstice.modules.notifications.data.PlayerNotificationSettings;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class NotificationsModule extends ModuleBase.Toggleable {
    private static NotificationsModule instance;
    public NotificationsModule(SolsticeIdentifier id) {
        super(id);
        instance = this;
    }

    @Override
    public void init() {
        registerConfig(NotificationsConfig.class, NotificationsConfig::new);
        registerLocale(NotificationsLocale.MODULE);
        registerPlayerData(NotificationsPlayerData.class, NotificationsPlayerData::new);

        commands.add(new NotificationsCommand(this));

        ServerMessageEvents.CHAT_MESSAGE.register((message, sourcePlayer, parameters) -> {
            var content = message.decoratedContent().getString().toLowerCase();

            sourcePlayer.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (player.equals(sourcePlayer)) {
                    return;
                }

                var playerName = PlayerUtils.getName(player.getGameProfile()).toLowerCase();
                if (content.contains(playerName)) {
                    var settings = getPlayerSettings(player);
                    if (settings.onChat()) {
                        notifyPlayer(player);
                    }
                }
            });
        });
    }

    public static void notify(ServerPlayer player) {
        if (!instance.isEnabled())
            return;

        instance.notifyPlayer(player);
    }

    public NotificationsConfig getConfig() {
        return Solstice.configManager.getData(NotificationsConfig.class);
    }

    public NotificationsPlayerData getPlayerData(ServerPlayer player) {
        return Solstice.playerData.get(player).getData(NotificationsPlayerData.class);
    }

    public PlayerNotificationSettings getPlayerSettings(ServerPlayer player) {
        var data = getPlayerData(player);
        var config = getConfig();

        return new PlayerNotificationSettings(
                data.soundId != null ? data.soundId : config.defaultValues.soundId,
                data.pitch != null ? data.pitch : config.defaultValues.pitch,
                data.volume != null ? data.volume : config.defaultValues.volume,
                data.afkOnly != null ? data.afkOnly : config.defaultValues.afkOnly,
                data.onChat != null ? data.onChat : config.defaultValues.onChat
        );
    }

    public boolean shouldNotify(ServerPlayer player) {
        if (!isEnabled())
            return false;

        var data = getPlayerData(player);
        var settings = getPlayerSettings(player);

        if (!data.enable)
            return false;

        var afkModule = ModuleProvider.AFK;
        if (afkModule.isEnabled()) {
            return afkModule.isPlayerAfk(player) || !settings.afkOnly();
        }

        return true;
    }

    public void notifyPlayer(ServerPlayer player) {
        if (!shouldNotify(player))
            return;

        var settings = getPlayerSettings(player);
        var id = SolsticeIdentifier.tryParse(settings.soundId());
        if (id == null) {
            return;
        }

        PlayerUtils.playSound(player,SoundEvent.createVariableRangeEvent(id.get()),settings.volume(),settings.pitch());
    }
}
