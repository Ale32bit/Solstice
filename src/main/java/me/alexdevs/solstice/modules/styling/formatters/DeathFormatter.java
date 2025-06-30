package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.styling.StylingModule;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatTracker;

import java.util.Map;

public class DeathFormatter {
    public static Component onDeath(ServerPlayer player, CombatTracker instance) {
        var config = ModuleProvider.STYLING.getConfig();
        var deathMessage = instance.getDeathMessage();
        var playerContext = PlaceholderContext.of(player);

        return Format.parse(
                config.deathFormat,
                playerContext,
                Map.of("message", deathMessage)
        );
    }
}
