package me.alexdevs.solstice.modules.customname;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.TextParserUtils;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.customname.commands.NicknameCommand;
import me.alexdevs.solstice.modules.customname.data.CustomNameConfig;
import me.alexdevs.solstice.modules.customname.data.CustomNamePlayerData;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomNameModule {
    public static final String ID = "customName";

    private final ConcurrentHashMap<UUID, String> namesCache = new ConcurrentHashMap<>();

    public CustomNameModule() {
        Solstice.configManager.registerData(ID, CustomNameConfig.class, CustomNameConfig::new);
        Solstice.playerData.registerData(ID, CustomNamePlayerData.class, CustomNamePlayerData::new);

        CommandRegistrationCallback.EVENT.register(NicknameCommand::new);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> refreshNames());
        ServerPlayConnectionEvents.JOIN.register((handler, packetSender, server) -> refreshNames());
    }

    public void refreshNames() {
        namesCache.clear();

        for (var player : Solstice.server.getPlayerManager().getPlayerList()) {
            refreshName(player);
        }
    }

    public void refreshName(ServerPlayerEntity player) {
        namesCache.put(player.getUuid(), fetchUsernameFormat(player));
    }

    public String fetchUsernameFormat(ServerPlayerEntity player) {
        var formats = Solstice.configManager.getData(CustomNameConfig.class).nameFormats;

        String format = null;
        for (var f : formats) {
            if (Permissions.check(player, "group." + f.group())) {
                format = f.format();
                break;
            }
        }

        var isOperator = player.getServer().getPlayerManager().isOperator(player.getGameProfile());

        if (format == null) {
            format = "${username}";

            for (var f : formats) {
                if (isOperator && f.group().equals("operator")) {
                    format = f.format();
                    break;
                }
                if (f.group().equals("default")) {
                    format = f.format();
                    break;
                }
            }
        }

        return format;
    }

    public MutableText getNameForPlayer(ServerPlayerEntity player) {
        var format = namesCache.get(player.getUuid());
        if (format == null) {
            // to avoid stack overflow we push the plain text version of the player
            namesCache.put(player.getUuid(), player.getGameProfile().getName());

            format = fetchUsernameFormat(player);
            namesCache.put(player.getUuid(), format);
        }

        var playerData = Solstice.playerData.get(player).getData(CustomNamePlayerData.class);

        var name = playerData.nickname == null ? Text.of(player.getGameProfile().getName()) : TextParserUtils.formatText(playerData.nickname);

        var placeholders = Map.of(
                "name", name
        );

        var playerContext = PlaceholderContext.of(player);
        return Format.parse(format, playerContext, placeholders).copy();
    }

    public void setCustomName(ServerPlayerEntity player, String name) {
        var playerData = Solstice.playerData.get(player).getData(CustomNamePlayerData.class);
        playerData.nickname = name;
        refreshName(player);
    }

    public void clearCustomName(ServerPlayerEntity player) {
        var playerData = Solstice.playerData.get(player).getData(CustomNamePlayerData.class);
        playerData.nickname = null;
        refreshName(player);
    }
}