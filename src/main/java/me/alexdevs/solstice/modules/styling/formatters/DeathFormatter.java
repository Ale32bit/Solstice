package me.alexdevs.solstice.modules.styling.formatters;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class DeathFormatter {
    public static Text onDeath(ServerPlayerEntity player, DamageTracker instance) {
        var locale = Solstice.localeManager.getLocale(StylingModule.ID);
        var deathMessage = instance.getDeathMessage();
        var playerContext = PlaceholderContext.of(player);

        return locale.get(
                "deathFormat",
                playerContext,
                Map.of("message", deathMessage)
        );
    }
}
