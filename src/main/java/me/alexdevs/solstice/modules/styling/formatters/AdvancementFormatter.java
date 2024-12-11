package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.util.Format;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;



import java.util.Map;

public class AdvancementFormatter {
    public static Text getText(ServerPlayerEntity player, AdvancementEntry entry, AdvancementFrame frame) {
        //var title = advancementKey + ".title";
        //var description = advancementKey + ".description";

        var locale = Solstice.localeManager.getLocale(StylingModule.ID);

        var title = entry.value().display().get().getTitle();
        var description = entry.value().display().get().getDescription();

        String advancementFormat = switch (frame) {
            case GOAL -> locale.raw("advancementGoal");
            case CHALLENGE -> locale.raw("advancementChallenge");
            default -> locale.raw("advancementTask");
        };

        var playerContext = PlaceholderContext.of(player);

        Map<String, Text> placeholders = Map.of(
                "frame", frame.getToastText(),
                "title", title,
                "description", description
        );

        return Format.parse(advancementFormat, playerContext, placeholders);
    }
}
