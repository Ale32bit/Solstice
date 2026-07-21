package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.ModModuleProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;


public class AdvancementFormatter {
    public static Component getText(ServerPlayer player, AdvancementHolder entry, AdvancementType frame) {
        var title = entry.value().display().get().getTitle();
        var description = entry.value().display().get().getDescription();

        var config = ModModuleProvider.STYLING.getConfig();

        String advancementFormat = switch (frame) {
            case GOAL -> config.advancementGoal;
            case CHALLENGE -> config.advancementChallenge;
            case TASK -> config.advancementTask;
        };

        var playerContext = PlaceholderContext.of(player);

        Map<String, Component> placeholders = Map.of(
                "frame", frame.getDisplayName(),
                "title", title,
                "description", description
        );

        return Format.parse(advancementFormat, playerContext, placeholders);
    }
}
