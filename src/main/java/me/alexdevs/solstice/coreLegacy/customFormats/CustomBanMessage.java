package me.alexdevs.solstice.coreLegacy.customFormats;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.util.Format;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.Map;

public class CustomBanMessage {
    public static Text format(GameProfile profile, BannedPlayerEntry entry) {
        var df = new SimpleDateFormat(ServiceProvider.config().formats.dateFormat);

        var context = PlaceholderContext.of(profile, ServiceProvider.server);
        var expiryDate = Text.of(entry.getExpiryDate() != null ? df.format(entry.getExpiryDate()) : "never");
        Map<String, Text> placeholders = Map.of(
                "reason", Format.parse(entry.getReason(), context),
                "creation_date", Text.of(df.format(entry.getCreationDate())),
                "expiry_date", expiryDate,
                "source", Text.of(entry.getSource())
        );

        var format = entry.getExpiryDate() != null ? ServiceProvider.config().formats.tempBanMessageFormat : ServiceProvider.config().formats.banMessageFormat;

        return Format.parse(format, context, placeholders);
    }
}
