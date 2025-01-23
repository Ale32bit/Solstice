package me.alexdevs.solstice.modules.staffChat;

import eu.pb4.placeholders.api.node.TextNode;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.parser.MarkdownParser;
import me.alexdevs.solstice.modules.staffChat.commands.StaffChatCommand;
import me.alexdevs.solstice.modules.staffChat.data.StaffChatLocale;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffChatModule extends ModuleBase.Toggleable {
    public static final String ID = "staffchat";
    private final ConcurrentHashMap<UUID, Boolean> stickyStaffChat = new ConcurrentHashMap<>();

    public StaffChatModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, StaffChatLocale.MODULE);

        commands.add(new StaffChatCommand(this));

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, pars) -> {
            if (stickyStaffChat.getOrDefault(player.getUuid(), false)
                    && canUseStaffChat(player)) {

                sendStaffChatMessage(player.getDisplayName(), message.getContent());

                return false;
            }
            return true;
        });
    }

    public boolean canUseStaffChat(ServerPlayerEntity player) {
        return Permissions.check(player, getPermissionNode("base"), 1);
    }

    public void sendStaffChatMessage(Text sourceName, final Text message) {
        var formattedMessage = MarkdownParser.defaultParser.parseNode(TextNode.convert(message)).toText();


        var text = Solstice.localeManager.getLocale(ID).get("message", Map.of(
                "name", sourceName,
                "message", formattedMessage
        ));

        Solstice.server.sendMessage(text);
        Solstice.server.getPlayerManager().getPlayerList().forEach(player -> {
            if (canUseStaffChat(player)) {
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
