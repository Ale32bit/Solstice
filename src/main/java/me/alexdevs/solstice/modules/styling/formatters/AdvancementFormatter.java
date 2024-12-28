package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.api.text.Format;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class AdvancementFormatter {
    public static Text getText(ServerPlayerEntity player, String advancementKey, String frameId) {
        var locale = Solstice.localeManager.getLocale(StylingModule.ID);
        var frame = AdvancementFrame.forName(frameId);
        var title = advancementKey + ".title";
        var description = advancementKey + ".description";

        var config = Solstice.modules.getModule(StylingModule.class).getConfig();

        String advancementFormat = switch (frame) {
            case GOAL -> config.advancementGoal;
            case CHALLENGE -> config.advancementChallenge;
            case TASK -> config.advancementTask;
        };

        var playerContext = PlaceholderContext.of(player);

        var placeholders = Map.of(
                "frame", Text.of(frameId),
                "title", Text.translatable(title),
                "description", Text.translatable(description)
        );

        return Format.parse(advancementFormat, playerContext, placeholders);
    }
}
