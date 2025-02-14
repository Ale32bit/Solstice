package me.alexdevs.solstice.core.coreModule.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class PingCommand extends ModCommand<CoreModule> {
    public PingCommand(CoreModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("ping");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("ping.base", true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var ping = player.pingMilliseconds;
                    var map = Map.of(
                            "ping", Text.of(String.valueOf(ping))
                    );
                    context.getSource().sendFeedback(() -> module.locale().get("ping.self", map), false);
                    return 1;
                })
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .requires(require("ping.others", 1))
                        .executes(context -> {
                            var player = EntityArgumentType.getPlayer(context, "player");
                            var ping = player.pingMilliseconds;
                            var map = Map.of(
                                    "ping", Text.of(String.valueOf(ping)),
                                    "player", player.getName()
                            );
                            context.getSource().sendFeedback(() -> module.locale().get("ping.other", map), false);
                            return 1;
                        })
                );
    }
}
