package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class AdvancementFormatter {
    public static Component getText(ServerPlayer player, Advancement advancement) {
        var frame = advancement.getDisplay().getFrame();
        var frameId = frame.getName();
        var title = advancement.getDisplay().getTitle();
        var description = advancement.getDisplay().getDescription();

        var config = ModuleProvider.STYLING.getConfig();

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
