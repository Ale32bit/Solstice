package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.ModuleProvider;
//? if >= 1.21.1 {
/*import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
*///? } else {
import net.minecraft.advancements.Advancement;
//? }
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class AdvancementFormatter {
    //? if >= 1.21.1 {
    /*public static Component getText(ServerPlayer player, AdvancementHolder entry, AdvancementType frame) {
        var title = entry.value().display().get().getTitle();
        var description = entry.value().display().get().getDescription();
    *///? } else {
    public static Component getText(ServerPlayer player, Advancement advancement) {
        var frame = advancement.getDisplay().getFrame();
        var frameId = frame.getName();
        var title = advancement.getDisplay().getTitle();
        var description = advancement.getDisplay().getDescription();
    //? }

        var config = ModuleProvider.STYLING.getConfig();

        String advancementFormat = switch (frame) {
            case GOAL -> config.advancementGoal;
            case CHALLENGE -> config.advancementChallenge;
            case TASK -> config.advancementTask;
        };

        var playerContext = PlaceholderContext.of(player);

        Map<String, Component> placeholders = Map.of(
                //? if >= 1.21.1 {
                /*"frame", frame.getDisplayName(),
                *///? } else {
                "frame", frame.getDisplayName(),
                //? }
                "title", title,
                "description", description
        );

        return Format.parse(advancementFormat, playerContext, placeholders);
    }
}
