package me.alexdevs.solstice.modules.mail;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.PlayerMail;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.mail.commands.MailCommand;
import me.alexdevs.solstice.modules.mail.data.MailLocale;
import me.alexdevs.solstice.modules.mail.data.MailPlayerData;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.List;
import java.util.UUID;

public class MailModule {
    public static final String ID = "mail";

    public final Locale locale;

    public MailModule() {
        Solstice.localeManager.registerModule(ID, MailLocale.MODULE);
        Solstice.playerData.registerData(ID, MailPlayerData.class, MailPlayerData::new);

        this.locale = Solstice.localeManager.getLocale(ID);

        CommandRegistrationCallback.EVENT.register(MailCommand::new);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.getPlayer();
            var playerContext = PlaceholderContext.of(player);

            if (!getMailData(player.getUuid()).mails.isEmpty()) {
                player.sendMessage(locale.get("mailPending", playerContext));
            }
        });
    }

    public void sendMail(UUID playerUuid, PlayerMail mail) {
        var playerData = ModerationModule.getPlayerData(playerUuid);
        if (playerData.ignoredPlayers.contains(mail.sender)) {
            return;
        }
        getMailData(playerUuid).mails.add(mail);
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
