package me.alexdevs.solstice.modules.commandSpy;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.CommandEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.commandSpy.data.CommandSpyConfig;
import me.alexdevs.solstice.modules.commandSpy.data.CommandSpyLocale;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.chat.Component;
import java.util.Map;

public class CommandSpyModule extends ModuleBase.Toggleable {
    public static final String ID = "commandspy";

    public CommandSpyModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, CommandSpyConfig.class, CommandSpyConfig::new);
        Solstice.localeManager.registerModule(ID, CommandSpyLocale.MODULE);

        CommandEvents.ALLOW_COMMAND.register((source, command) -> {
            if (!source.isPlayer())
                return true;

            Solstice.LOGGER.info("{}: /{}", source.getTextName(), command);

            var parts = command.split("\\s");
            if (parts.length >= 1) {
                var cmd = parts[0];
                if (isIgnored(cmd)) {
                    return true;
                }
            }

            var player = source.getPlayer();

            var players = source.getServer().getPlayerList().getPlayers();
            var placeholders = Map.of("player", Component.nullToEmpty(player.getGameProfile().getName()), "command", Component.nullToEmpty(command));
            var message = locale().get("spyFormat", placeholders);
            for (var pl : players) {
                var commandSpyEnabled = Permissions.check(pl, this.getPermissionNode("base"));

                if (commandSpyEnabled && !pl.getUUID().equals(player.getUUID())) {
                    pl.displayClientMessage(message, false);
                }
            }
            return true;
        });

    }

    public boolean isIgnored(String command) {
        if(!isEnabled())
            return false;

        return Solstice.configManager.getData(CommandSpyConfig.class).ignoredCommands.contains(command);
    }
}
