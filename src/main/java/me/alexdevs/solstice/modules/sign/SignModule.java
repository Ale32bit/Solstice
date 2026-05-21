package me.alexdevs.solstice.modules.sign;

import eu.pb4.placeholders.api.parsers.LegacyFormattingParser;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignText;
import java.util.List;

public class SignModule extends ModuleBase.Toggleable {

    public SignModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
    }

    public static SignText formatSign(List<FilteredText> messages, SignText text) {
        for (var i = 0; i < messages.size(); i++) {
            var message = messages.get(i);
            var line = message.raw();
            text = text.setMessage(i, LegacyFormattingParser.ALL.parseNode(line).toText());
        }
        return text;
    }

    public boolean canFormatSign(Player player) {
        return Permissions.check(player, getPermissionNode("format"), 2);
    }
}
