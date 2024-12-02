package me.alexdevs.solstice.coreLegacy.customFormats;

import me.alexdevs.solstice.core.ServiceProvider;
import me.alexdevs.solstice.util.Format;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class CustomConnectionMessage {
    public static Text onJoin(ServerPlayerEntity player) {
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                ServiceProvider.config().formats.joinFormat,
                playerContext
        );
    }

    public static Text onJoinRenamed(ServerPlayerEntity player, String previousName) {
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                ServiceProvider.config().formats.joinRenamedFormat,
                playerContext,
                Map.of("previousName", Text.of(previousName))
        );
    }

    public static Text onLeave(ServerPlayerEntity player) {
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                ServiceProvider.config().formats.leaveFormat,
                playerContext
        );
    }
}
