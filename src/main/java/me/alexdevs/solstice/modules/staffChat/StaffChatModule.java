package me.alexdevs.solstice.modules.staffChat;

import eu.pb4.placeholders.api.node.TextNode;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.parser.MarkdownParser;
import me.alexdevs.solstice.modules.staffChat.commands.StaffChatCommand;
import me.alexdevs.solstice.modules.staffChat.data.StaffChatLocale;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class StaffChatModule extends ModuleBase.Toggleable {
    
    private final ConcurrentHashMap<UUID, Boolean> stickyStaffChat = new ConcurrentHashMap<>();

    public StaffChatModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(StaffChatLocale.MODULE);

        commands.add(new StaffChatCommand(this));

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, pars) -> {
            if (stickyStaffChat.getOrDefault(player.getUUID(), false)
                    && canUseStaffChat(player)) {

                sendStaffChatMessage(player.getDisplayName(), message.decoratedContent());

                return false;
            }
            return true;
        });
    }

    public boolean canUseStaffChat(ServerPlayer player) {
        return Permissions.check(player, getPermissionNode("base"), 1);
    }

    public void sendStaffChatMessage(Component sourceName, final Component message) {
        var formattedMessage = MarkdownParser.defaultParser.parseNode(TextNode.convert(message)).toText();


        var text = locale().get("message", Map.of(
                "name", sourceName,
                "message", formattedMessage
        ));

        Solstice.server.sendSystemMessage(text);
        Solstice.server.getPlayerList().getPlayers().forEach(player -> {
            if (canUseStaffChat(player)) {
                player.displayClientMessage(text, false);
            }
        });
    }

    public boolean toggleStaffChat(UUID uuid) {
        var val = !stickyStaffChat.getOrDefault(uuid, false);
        stickyStaffChat.put(uuid, val);
        return val;
    }
}
