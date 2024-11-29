package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomNameFormat {

    private static final ConcurrentHashMap<UUID, String> namesCache = new ConcurrentHashMap<>();

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> refreshNames());
    }

    public static void refreshNames() {
        namesCache.clear();

        for (var player : Solstice.server.getPlayerManager().getPlayerList()) {
            namesCache.put(player.getUuid(), fetchUsernameFormat(player));
        }
    }

    public static String fetchUsernameFormat(ServerPlayerEntity player) {
        var formats = Solstice.config().formats.nameFormats;

        String format = null;
        for (var f : formats) {
            if (Permissions.check(player, "group." + f.group())) {
                format = f.format();
                break;
            }
        }

        var isOperator = player.getServer().getPlayerManager().isOperator(player.getGameProfile());

        if (format == null) {
            format = "%player:name%";

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

    public static MutableText getNameForPlayer(ServerPlayerEntity player) {
        var format = namesCache.get(player.getUuid());
        if(format == null) {
            // to avoid stack overflow we push the plain text version of the player
            namesCache.put(player.getUuid(), player.getGameProfile().getName());

            format = fetchUsernameFormat(player);
            namesCache.put(player.getUuid(), format);
        }

        var playerContext = PlaceholderContext.of(player);
        var output = TextParserUtils.formatText(format);
        return Placeholders.parseText(output, playerContext).copy();
    }
}
