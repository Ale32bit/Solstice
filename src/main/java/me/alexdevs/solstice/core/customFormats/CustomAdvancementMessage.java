package me.alexdevs.solstice.core.customFormats;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class CustomAdvancementMessage {
    public static Text getText(ServerPlayerEntity player, AdvancementEntry entry, AdvancementFrame frame) {
        //var title = advancementKey + ".title";
        //var description = advancementKey + ".description";

        var title = entry.value().display().get().getTitle();
        var description = entry.value().display().get().getDescription();

        var formats = Solstice.config().formats.advancementFormats;

        String advancementFormat = switch (frame) {
            case GOAL -> formats.goal;
            case CHALLENGE -> formats.challenge;
            default -> formats.task;
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
