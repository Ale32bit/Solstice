package me.alexdevs.solstice.modules.styling.formatters;

import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.Solstice;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class ConnectionActivityFormatter {
    public static Text onJoin(ServerPlayerEntity player) {
        var locale = Solstice.localeManager.getLocale(StylingModule.ID);
        var playerContext = PlaceholderContext.of(player);
        return locale.get(
                "joinFormat",
                playerContext
        );
    }

    public static Text onJoinRenamed(ServerPlayerEntity player, String previousName) {
        var locale = Solstice.localeManager.getLocale(StylingModule.ID);
        var playerContext = PlaceholderContext.of(player);
        return locale.get(
                "joinRenamedFormat",
                playerContext,
                Map.of("previousName", Text.of(previousName))
        );
    }

    public static Text onLeave(ServerPlayerEntity player) {
        var locale = Solstice.localeManager.getLocale(StylingModule.ID);
        var playerContext = PlaceholderContext.of(player);
        return locale.get(
                "leaveFormat",
                playerContext
        );
    }
}
