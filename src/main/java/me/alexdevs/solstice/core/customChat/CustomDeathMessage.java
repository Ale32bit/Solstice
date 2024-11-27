package me.alexdevs.solstice.core.customChat;

import me.alexdevs.solstice.Solstice;
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
                Solstice.locale().deathFormat,
                playerContext,
                Map.of("message", deathMessage)
        );
    }
}
