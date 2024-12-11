package me.alexdevs.solstice.modules.formattablesigns;

import eu.pb4.placeholders.api.parsers.LegacyFormattingParser;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.filter.FilteredMessage;

import java.util.List;

public class FormattableSignsModule {
    public static final String ID = "formattableSigns";
    public static final String PERMISSION = "solstice.sign.format";

    public static boolean canFormatSign(PlayerEntity player) {
        return Permissions.check(player, PERMISSION, 2);
    }

    public static SignText formatSign(List<FilteredMessage> messages, SignText text) {
        for (var i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            var line = message.raw();
            text = text.withMessage(i, LegacyFormattingParser.ALL.parseNode(line).toText());
        }
        return text;
    }
}