package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.text.Text;

import java.util.Map;

public class CommandSpy {
    public static void register() {
        SolsticeEvents.PLAYER_COMMAND.register((source, command) -> {
            var parts = command.split("\\s");
            if(parts.length >= 1) {
                var cmd = parts[0];
                if(Solstice.config().commandSpy.ignoredCommands.contains(cmd)) {
                    return;
                }
            }

            var players = source.getServer().getPlayerManager().getPlayerList();
            var placeholders = Map.of(
                    "player", Text.of(source.getGameProfile().getName()),
                    "command", Text.of(command)
            );
            var message = Format.parse(Solstice.config().commandSpy.commandSpyFormat, placeholders);
            for(var player : players) {
                var commandSpyEnabled = Permissions.check(player, "solstice.commandspy");

                if(commandSpyEnabled && !player.getUuid().equals(source.getUuid())) {
                    player.sendMessage(message, false);
                }
            }
        });
    }
}
