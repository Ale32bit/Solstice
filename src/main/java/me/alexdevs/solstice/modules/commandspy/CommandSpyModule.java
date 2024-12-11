package me.alexdevs.solstice.modules.commandspy;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.modules.commandspy.data.CommandSpyConfig;
import me.alexdevs.solstice.modules.commandspy.data.CommandSpyLocale;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.text.Text;

import java.util.Map;

public class CommandSpyModule {
    public static final String ID = "commandspy";
    public static final String PERMISSION = "solstice.commandspy";

    public CommandSpyModule() {
        Solstice.configManager.registerData(ID, CommandSpyConfig.class, CommandSpyConfig::new);
        Solstice.localeManager.registerModule(ID, CommandSpyLocale.MODULE);

        var locale = Solstice.localeManager.getLocale(ID);

        SolsticeEvents.PLAYER_COMMAND.register((source, command) -> {
            var parts = command.split("\\s");
            if (parts.length >= 1) {
                var cmd = parts[0];
                if (isIgnored(cmd)) {
                    return;
                }
            }

            var players = source.getServer().getPlayerManager().getPlayerList();
            var placeholders = Map.of("player", Text.of(source.getGameProfile().getName()), "command", Text.of(command));
            var message = locale.get("spyFormat", placeholders);
            for (var player : players) {
                var commandSpyEnabled = Permissions.check(player, PERMISSION);

                if (commandSpyEnabled && !player.getUuid().equals(source.getUuid())) {
                    player.sendMessage(message, false);
                }
            }
        });
    }

    public boolean isIgnored(String command) {
        return Solstice.configManager.getData(CommandSpyConfig.class).ignoredCommands.contains(command);
    }
}
