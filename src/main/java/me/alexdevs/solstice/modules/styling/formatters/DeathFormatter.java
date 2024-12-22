package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.util.Format;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class DeathFormatter {
    public static Text onDeath(ServerPlayerEntity player, DamageTracker instance) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var deathMessage = instance.getDeathMessage();
        var playerContext = PlaceholderContext.of(player);

        return Format.parse(
                config.deathFormat,
                playerContext,
                Map.of("message", deathMessage)
        );
    }
}
