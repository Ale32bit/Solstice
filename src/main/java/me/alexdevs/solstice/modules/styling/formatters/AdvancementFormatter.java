package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import me.alexdevs.solstice.api.text.Format;
import java.util.Map;

public class AdvancementFormatter {
    public static Component getText(ServerPlayer player, Advancement advancement) {
        var frame = advancement.getDisplay().getFrame();
        var frameId = frame.getName();
        var title = advancement.getDisplay().getTitle().getString();
        var description = advancement.getDisplay().getDescription().getString();

        var config = Solstice.modules.getModule(StylingModule.class).getConfig();

        String advancementFormat = switch (frame) {
            case GOAL -> config.advancementGoal;
            case CHALLENGE -> config.advancementChallenge;
            case TASK -> config.advancementTask;
        };

        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "frame", Component.nullToEmpty(frameId),
                "title", Component.translatable(title),
                "description", Component.translatable(description)
        );

        return Format.parse(advancementFormat, playerContext, placeholders);
    }
}
