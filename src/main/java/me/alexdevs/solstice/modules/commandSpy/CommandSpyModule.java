package me.alexdevs.solstice.modules.commandSpy;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.CommandEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.commandSpy.data.CommandSpyConfig;
import me.alexdevs.solstice.modules.commandSpy.data.CommandSpyLocale;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.text.Text;

import java.util.Map;

public class CommandSpyModule extends ModuleBase {
    public static final String ID = "commandspy";

    public CommandSpyModule() {
        super(ID);

        Solstice.configManager.registerData(ID, CommandSpyConfig.class, CommandSpyConfig::new);
        Solstice.localeManager.registerModule(ID, CommandSpyLocale.MODULE);

        CommandEvents.ALLOW_COMMAND.register((source, command) -> {
            if (!source.isExecutedByPlayer())
                return true;

            Solstice.LOGGER.info("{}: /{}", source.getName(), command);

            var parts = command.split("\\s");
            if (parts.length >= 1) {
                var cmd = parts[0];
                if (isIgnored(cmd)) {
                    return true;
                }
            }

            var player = source.getPlayer();

            var players = source.getServer().getPlayerManager().getPlayerList();
            var placeholders = Map.of("player", Text.of(player.getGameProfile().getName()), "command", Text.of(command));
            var message = locale().get("spyFormat", placeholders);
            for (var pl : players) {
                var commandSpyEnabled = Permissions.check(pl, this.getPermissionNode("base"));

                if (commandSpyEnabled && !pl.getUuid().equals(player.getUuid())) {
                    pl.sendMessage(message, false);
                }
            }
            return true;
        });

    }

    public boolean isIgnored(String command) {
        return Solstice.configManager.getData(CommandSpyConfig.class).ignoredCommands.contains(command);
    }
}
