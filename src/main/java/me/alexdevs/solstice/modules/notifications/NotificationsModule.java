package me.alexdevs.solstice.modules.notifications;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.notifications.commands.NotificationsCommand;
import me.alexdevs.solstice.modules.notifications.data.NotificationsConfig;
import me.alexdevs.solstice.modules.notifications.data.NotificationsLocale;
import me.alexdevs.solstice.modules.notifications.data.NotificationsPlayerData;
import me.alexdevs.solstice.modules.notifications.data.PlayerNotificationSettings;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class NotificationsModule extends ModuleBase.Toggleable {
    public static final String ID = "notifications";

    public NotificationsModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, NotificationsConfig.class, NotificationsConfig::new);
        Solstice.localeManager.registerModule(ID, NotificationsLocale.MODULE);
        Solstice.playerData.registerData(ID, NotificationsPlayerData.class, NotificationsPlayerData::new);

        commands.add(new NotificationsCommand(this));

        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, parameters) -> {
            var content = message.getContent().getString().toLowerCase();

            sender.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                if(player.equals(sender)) {
                    return;
                }

                var playerName = player.getGameProfile().getName().toLowerCase();
                if (content.contains(playerName)) {
                    var settings = getPlayerSettings(player);
                    if (settings.onChat()) {
                        notifyPlayer(player);
                    }
                }
            });
        });
    }

    public static void notify(ServerPlayerEntity player) {
        var module = Solstice.modules.getModule(NotificationsModule.class);
        if (!module.isEnabled())
            return;

        module.notifyPlayer(player);
    }

    public NotificationsConfig getConfig() {
        return Solstice.configManager.getData(NotificationsConfig.class);
    }

    public NotificationsPlayerData getPlayerData(ServerPlayerEntity player) {
        return Solstice.playerData.get(player).getData(NotificationsPlayerData.class);
    }

    public PlayerNotificationSettings getPlayerSettings(ServerPlayerEntity player) {
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

    public boolean shouldNotify(ServerPlayerEntity player) {
        if (!isEnabled())
            return false;

        var data = getPlayerData(player);
        var settings = getPlayerSettings(player);

        if (!data.enable)
            return false;

        var afkModule = Solstice.modules.getModule(AfkModule.class);
        if (afkModule.isEnabled()) {
            return afkModule.isPlayerAfk(player) || !settings.afkOnly();
        }

        return true;
    }

    public void notifyPlayer(ServerPlayerEntity player) {
        if (!shouldNotify(player))
            return;

        var settings = getPlayerSettings(player);
        var id = Identifier.tryParse(settings.soundId());
        if (id == null) {
            return;
        }

        player.playSound(SoundEvent.of(id), SoundCategory.MASTER, settings.volume(), settings.pitch());
    }
}
