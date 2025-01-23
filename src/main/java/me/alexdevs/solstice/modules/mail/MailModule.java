package me.alexdevs.solstice.modules.mail;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.PlayerMail;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.mail.commands.MailCommand;
import me.alexdevs.solstice.modules.mail.data.MailLocale;
import me.alexdevs.solstice.modules.mail.data.MailPlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MailModule extends ModuleBase.Toggleable {
    public static final String ID = "mail";

    public MailModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, MailLocale.MODULE);
        Solstice.playerData.registerData(ID, MailPlayerData.class, MailPlayerData::new);

        commands.add(new MailCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var playerContext = PlaceholderContext.of(player);

            Solstice.scheduler.schedule(() -> {
                if (!getMailData(player.getUuid()).mails.isEmpty()) {
                    player.sendMessage(locale().get("mailPending", playerContext));
                }
            }, 1, TimeUnit.SECONDS);
        });
    }

    public boolean sendMail(UUID playerUuid, PlayerMail mail) {
        var ignoreModule = Solstice.modules.getModule(IgnoreModule.class);
        var playerData = ignoreModule.getPlayerData(playerUuid);
        if (playerData.ignoredPlayers.contains(mail.sender)) {
            return false;
        }
        getMailData(playerUuid).mails.add(mail);
        return true;
    }

    public List<PlayerMail> getMailList(UUID playerUuid) {
        return getMailData(playerUuid).mails.stream().toList();
    }

    public boolean deleteMail(UUID playerUuid, int index) {
        var mailData = getMailData(playerUuid);
        if (index < 0 || index >= mailData.mails.size()) {
            return false;
        }
        mailData.mails.remove(index);
        return true;
    }

    public void clearAllMail(UUID playerUuid) {
        getMailData(playerUuid).mails.clear();
    }

    public MailPlayerData getMailData(UUID playerUuid) {
        return Solstice.playerData.get(playerUuid).getData(MailPlayerData.class);
    }
}
