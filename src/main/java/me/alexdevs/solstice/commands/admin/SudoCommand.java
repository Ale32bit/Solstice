package me.alexdevs.solstice.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

import static net.minecraft.server.command.CommandManager.*;

public class SudoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var doasCommand = literal("doas")
                .requires(Permissions.require("solstice.command.doas", 4))
                .then(argument("player", GameProfileArgumentType.gameProfile())
                        .then(argument("command", StringArgumentType.greedyString())
                                .executes(context -> {
                                    var profiles = GameProfileArgumentType.getProfileArgument(context, "player");
                                    var profileArgRange = context.getNodes().get(1).getRange();
                                    var stringProfiles = context.getInput().substring(
                                            profileArgRange.getStart(),
                                            profileArgRange.getEnd()
                                    );

                                    var command = StringArgumentType.getString(context, "command");

                                    context.getSource().sendFeedback(() -> Text.literal(String.format("Executing '%s' as %s", command, stringProfiles)), true);

                                    CommandOutput commandOutput;
                                    if (context.getSource().isExecutedByPlayer()) {
                                        commandOutput = context.getSource().getPlayer();
                                    } else {
                                        commandOutput = context.getSource().getServer();
                                    }

                                    var server = context.getSource().getServer();
                                    var playerManager = server.getPlayerManager();
                                    for (var profile : profiles) {
                                        var player = playerManager.getPlayer(profile.getId());
                                        var source = buildPlayerSource(commandOutput, server, player);
                                        execute(dispatcher, command, source, context.getSource());
                                    }

                                    return 1;
                                })
                        )
                );

        var sudoCommand = literal("sudo")
                .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> {
                            if(!Permissions.check(context.getSource(), "solstice.command.sudo", 4)) {
                                context.getSource().sendError(Text.literal(String.format("%s is not in the sudoers file. This incident will be reported.", context.getSource().getName()))
                                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://xkcd.com/838/"))));
                                return 1;
                            }
                            var command = StringArgumentType.getString(context, "command");

                            context.getSource().sendFeedback(() -> Text.literal(String.format("Executing '%s' as Server", command)), true);

                            CommandOutput commandOutput;
                            if (context.getSource().isExecutedByPlayer()) {
                                commandOutput = context.getSource().getPlayer();
                            } else {
                                commandOutput = context.getSource().getServer();
                            }

                            var server = context.getSource().getServer();
                            var source = buildServerSource(commandOutput, server);
                            execute(dispatcher, command, source, context.getSource());

                            return 1;
                        })
                );

        dispatcher.register(sudoCommand);
        dispatcher.register(doasCommand);
    }

    public static ServerCommandSource buildServerSource(CommandOutput commandOutput, MinecraftServer server) {
        return new ServerCommandSource(
                commandOutput,
                server.getOverworld().getSpawnPos().toCenterPos(),
                Vec2f.ZERO,
                server.getOverworld(),
                4,
                "Server",
                Text.of("Server"),
                server,
                null
        );
    }

    public static ServerCommandSource buildPlayerSource(CommandOutput commandOutput, MinecraftServer server, ServerPlayerEntity player) {
        var opList = server.getPlayerManager().getOpList();
        var operator = opList.get(player.getGameProfile());
        int opLevel = 0;
        if (operator != null) {
            opLevel = operator.getPermissionLevel();
        }
        return new ServerCommandSource(
                commandOutput,
                player.getPos(),
                player.getRotationClient(),
                player.getServerWorld(),
                opLevel,
                player.getEntityName(),
                player.getDisplayName(),
                server,
                player
        );
    }

    public static void execute(CommandDispatcher<ServerCommandSource> dispatcher, String command, ServerCommandSource source, ServerCommandSource output) {
        try {
            dispatcher.execute(command, source);
        } catch (Exception e) {
            output.sendError(Text.of(String.format("[%s] %s", source.getName(), e.getMessage())));
        }
    }

}
