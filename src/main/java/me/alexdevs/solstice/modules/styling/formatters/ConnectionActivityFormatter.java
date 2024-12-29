package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.api.text.Format;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class ConnectionActivityFormatter {
    public static Text onJoin(ServerPlayerEntity player) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                config.joinFormat,
                playerContext
        );
    }

    public static Text onJoinRenamed(ServerPlayerEntity player, String previousName) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                config.joinRenamedFormat,
                playerContext,
                Map.of("previousName", Text.of(previousName))
        );
    }

    public static Text onLeave(ServerPlayerEntity player) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                config.leaveFormat,
                playerContext
        );
    }
}
