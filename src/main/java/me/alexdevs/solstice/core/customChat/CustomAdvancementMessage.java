package me.alexdevs.solstice.core.customChat;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.text.Text;

import java.util.Map;

public class CustomAdvancementMessage {
    public static Text getText(Text playerDisplayName, String advancementKey, String frameId) {
        var frame = AdvancementFrame.forName(frameId);
        var title = advancementKey + ".title";
        var description = advancementKey + ".description";

        var formats = Solstice.config().formats.advancementFormats;

        String advancementFormat = switch (frame) {
            case GOAL -> formats.goal;
            case CHALLENGE -> formats.challenge;
            default -> formats.task;
        };

        var placeholders = Map.of(
                "player", playerDisplayName,
                "frame", Text.of(frameId),
                "title", Text.translatable(title),
                "description", Text.translatable(description)
        );

        return Format.parse(advancementFormat, placeholders);
    }
}
