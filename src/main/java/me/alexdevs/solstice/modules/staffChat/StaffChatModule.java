package me.alexdevs.solstice.modules.staffChat;

import eu.pb4.placeholders.api.node.TextNode;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.staffChat.commands.StaffChatCommand;
import me.alexdevs.solstice.modules.staffChat.data.StaffChatLocale;
import me.alexdevs.solstice.api.text.parser.MarkdownParser;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffChatModule extends ModuleBase {
    public static final String ID = "staffchat";
    private final ConcurrentHashMap<UUID, Boolean> stickyStaffChat = new ConcurrentHashMap<>();
    private StaffChatCommand scCommand;

    public StaffChatModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, StaffChatLocale.MODULE);

        commands.add(new StaffChatCommand(this));

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, pars) -> {
            if (stickyStaffChat.getOrDefault(player.getUuid(), false)
                    && scCommand.require(1).test(player.getCommandSource())) {

                sendStaffChatMessage(player.getDisplayName(), message.getContent());

                return false;
            }
            return true;
        });
    }

    public void sendStaffChatMessage(Text sourceName, final Text message) {
        var formattedMessage = MarkdownParser.defaultParser.parseNode(TextNode.convert(message)).toText();


        var text = Solstice.localeManager.getLocale(ID).get("message", Map.of(
                "name", sourceName,
                "message", formattedMessage
        ));

        Solstice.server.sendMessage(text);
        Solstice.server.getPlayerManager().getPlayerList().forEach(player -> {
            if (scCommand.require(1).test(player.getCommandSource())) {
                player.sendMessage(text, false);
            }
        });
    }

    public boolean toggleStaffChat(UUID uuid) {
        var val = !stickyStaffChat.getOrDefault(uuid, false);
        stickyStaffChat.put(uuid, val);
        return val;
    }
}
