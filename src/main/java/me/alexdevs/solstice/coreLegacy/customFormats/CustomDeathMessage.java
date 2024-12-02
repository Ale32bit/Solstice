package me.alexdevs.solstice.coreLegacy.customFormats;

import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class CustomDeathMessage {
    public static Text onDeath(ServerPlayerEntity player, DamageTracker instance) {
        var deathMessage = instance.getDeathMessage();
        var playerContext = PlaceholderContext.of(player);

        return Format.parse(
                ServiceProvider.config().formats.deathFormat,
                playerContext,
                Map.of("message", deathMessage)
        );
    }
}
