package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.util.Format;
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

        String advancementFormat = switch (frame) {
            case GOAL -> locale.raw("advancementGoal");
            case CHALLENGE -> locale.raw("advancementChallenge");
            default -> locale.raw("advancementTask");
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
