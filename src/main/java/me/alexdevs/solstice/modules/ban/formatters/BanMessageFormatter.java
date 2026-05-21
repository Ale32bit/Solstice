package me.alexdevs.solstice.modules.ban.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.utils.ProfileOrNameAndId;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.UserBanListEntry;

import java.text.SimpleDateFormat;
import java.util.Map;

public class BanMessageFormatter {
    public static Component format(ProfileOrNameAndId profile, UserBanListEntry entry) {
        var locale = ModuleProvider.BAN.locale();
        var coreConfig = CoreModule.getConfig();
        var df = new SimpleDateFormat(coreConfig.dateTimeFormat);
        var context = PlaceholderContext.of(profile.getProfile(), Solstice.server);
        var expiryDate = Component.nullToEmpty(entry.getExpires() != null ? df.format(entry.getExpires()) : "never");
        Map<String, Component> placeholders = Map.of(
                "reason", Format.parse(entry.getReason(), context),
                "creation_date", Component.nullToEmpty(df.format(entry.getCreated())),
                "expiry_date", expiryDate,
                "source", Component.nullToEmpty(entry.getSource())
        );

        var format = entry.getExpires() != null ? locale.raw("tempBanMessageFormat") : locale.raw("banMessageFormat");

        return Format.parse(format, context, placeholders);
    }
}
