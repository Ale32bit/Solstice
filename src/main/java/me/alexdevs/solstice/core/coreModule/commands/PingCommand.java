package me.alexdevs.solstice.core.coreModule.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("ping.base", true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    //? if >= 1.21.1 {
                    /*var ping = player.connection.latency();
                    *///? } else {
                    var ping = player.latency;
                    //? }
                    var map = Map.of(
                            "ping", Component.nullToEmpty(String.valueOf(ping))
                    );
                    context.getSource().sendSuccess(() -> module.locale().get("ping.self", map), false);
                    return 1;
                })
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(require("ping.others", 1))
                        .executes(context -> {
                            var player = EntityArgument.getPlayer(context, "player");
                    //? if >= 1.21.1 {
                    /*var ping = player.connection.latency();
                    *///? } else {
                    var ping = player.latency;
                    //? }
                    var map = Map.of(
                                    "ping", Component.nullToEmpty(String.valueOf(ping)),
                                    "player", player.getName()
                            );
                            context.getSource().sendSuccess(() -> module.locale().get("ping.other", map), false);
                            return 1;
                        })
                );
    }
}
