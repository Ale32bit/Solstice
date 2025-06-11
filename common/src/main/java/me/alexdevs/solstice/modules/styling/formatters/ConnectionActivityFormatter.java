package me.alexdevs.solstice.modules.styling.formatters;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.StylingModule;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import me.alexdevs.solstice.api.text.Format;
import java.util.Map;

public class ConnectionActivityFormatter {
    public static Component onJoin(ServerPlayer player) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                config.joinFormat,
                playerContext
        );
    }

    public static Component onJoinRenamed(ServerPlayer player, String previousName) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                config.joinRenamedFormat,
                playerContext,
                Map.of("previousName", Component.nullToEmpty(previousName))
        );
    }

    public static Component onLeave(ServerPlayer player) {
        var config = Solstice.modules.getModule(StylingModule.class).getConfig();
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(
                config.leaveFormat,
                playerContext
        );
    }
}
