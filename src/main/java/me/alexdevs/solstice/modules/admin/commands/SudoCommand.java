package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

import java.util.List;

import static net.minecraft.server.command.CommandManager.*;

public class SudoCommand extends ModCommand {
    public SudoCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, RegistrationEnvironment environment) {
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
        return List.of("sudo");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> {
                            if(!Permissions.check(context.getSource(), getPermissionNode(), 4)) {
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
    }

    public ServerCommandSource buildServerSource(CommandOutput commandOutput, MinecraftServer server) {
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
}
