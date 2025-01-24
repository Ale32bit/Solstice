package me.alexdevs.solstice.modules.notification;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.notification.data.NotificationPlayerData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class NotificationModule extends ModuleBase.Toggleable {
    public static final String ID = "notification";

    public NotificationModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.playerData.registerData(ID, NotificationPlayerData.class, NotificationPlayerData::new);
    }

    public static void notify(ServerPlayerEntity player) {
        var module = Solstice.modules.getModule(NotificationModule.class);
        if (!module.isEnabled())
            return;

        module.notifyPlayer(player);
    }

    public NotificationPlayerData getPlayerData(ServerPlayerEntity player) {
        return Solstice.playerData.get(player).getData(NotificationPlayerData.class);
    }

    public boolean shouldNotify(ServerPlayerEntity player) {
        if (!isEnabled())
            return false;

        var data = getPlayerData(player);

        if (!data.enable)
            return false;

        var afkModule = Solstice.modules.getModule(AfkModule.class);
        if (afkModule.isEnabled()) {
            return afkModule.isPlayerAfk(player) || !data.afkOnly;
        }

        return true;
    }

    public void notifyPlayer(ServerPlayerEntity player) {
        if (!shouldNotify(player))
            return;

        var data = getPlayerData(player);
        var id = Identifier.tryParse(data.soundId);
        if (id == null) {
            return;
        }

        player.playSound(SoundEvent.of(id), SoundCategory.MASTER, data.volume, data.pitch);
    }
}
