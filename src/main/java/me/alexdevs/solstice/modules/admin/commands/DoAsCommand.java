package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DoAsCommand extends ModCommand {
    public DoAsCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    public static void execute(CommandDispatcher<ServerCommandSource> dispatcher, String command, ServerCommandSource source, ServerCommandSource output) {
        try {
            dispatcher.execute(command, source);
        } catch (Exception e) {
            output.sendError(Text.of(String.format("[%s] %s", source.getName(), e.getMessage())));
        }
    }

    @Override
    public List<String> getNames() {
        return List.of("doas");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(4))
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
                player.getGameProfile().getName(),
                player.getDisplayName(),
                server,
                player
        );
    }
}
