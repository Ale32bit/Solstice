package me.alexdevs.solstice.modules.sudo.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.sudo.SudoModule;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DoAsCommand extends ModCommand<SudoModule> {
    public DoAsCommand(SudoModule module) {
        super(module);
    }

    public static void execute(CommandDispatcher<CommandSourceStack> dispatcher, String command, CommandSourceStack source, CommandSourceStack output) {
        try {
            dispatcher.execute(command, source);
        } catch (Exception e) {
            output.sendFailure(Component.nullToEmpty(String.format("[%s] %s", source.getTextName(), e.getMessage())));
        }
    }

    public static CommandSourceStack buildPlayerSource(CommandSource commandOutput, MinecraftServer server, ServerPlayer player) {
        var opList = server.getPlayerList().getOps();
        var operator = opList.get(player.getGameProfile());
        int opLevel = 0;
        if (operator != null) {
            opLevel = operator.getLevel();
        }
        return new CommandSourceStack(
                commandOutput,
                player.position(),
                player.getRotationVector(),
                player.serverLevel(),
                opLevel,
                player.getScoreboardName(),
                player.getDisplayName(),
                server,
                player
        );
    }

    @Override
    public List<String> getNames() {
        return List.of("doas");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("doas", 4))
                .then(argument("player", EntityArgument.players())
                        .then(argument("command", StringArgumentType.greedyString())
                                .executes(context -> {
                                    var players = EntityArgument.getPlayers(context, "player");
                                    var profileArgRange = context.getNodes().get(1).getRange();
                                    var stringProfiles = context.getInput().substring(
                                            profileArgRange.getStart(),
                                            profileArgRange.getEnd()
                                    );

                                    var command = StringArgumentType.getString(context, "command");

                                    context.getSource().sendSuccess(() -> Component.literal(String.format("Executing '%s' as %s", command, stringProfiles)), true);

                                    CommandSource commandOutput;
                                    if (context.getSource().isPlayer()) {
                                        commandOutput = context.getSource().getPlayer();
                                    } else {
                                        commandOutput = context.getSource().getServer();
                                    }

                                    var server = context.getSource().getServer();
                                    for (var player : players) {
                                        var source = buildPlayerSource(commandOutput, server, player);
                                        execute(dispatcher, command, source, context.getSource());
                                    }

                                    return 1;
                                })
                        )
                );
    }
}
