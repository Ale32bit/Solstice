package me.alexdevs.solstice.modules.sudo.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.ComponentUtils;
import me.alexdevs.solstice.modules.sudo.SudoModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SudoCommand extends ModCommand<SudoModule> {
    public SudoCommand(SudoModule module) {
        super(module);
    }

    public static void execute(CommandDispatcher<CommandSourceStack> dispatcher, String command, CommandSourceStack source, CommandSourceStack output) {
        try {
            dispatcher.execute(command, source);
        } catch (Exception e) {
            output.sendFailure(Component.nullToEmpty(String.format("[%s] %s", source.getTextName(), e.getMessage())));
        }
    }

    @Override
    public List<String> getNames() {
        return List.of("sudo");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> {
                            if (!Permissions.check(context.getSource(), getPermissionNode("sudo"), 4)) {
                                context.getSource().sendFailure(Component.literal(String.format("%s is not in the sudoers file. This incident will be reported.", context.getSource().getTextName()))
                                        .setStyle(Style.EMPTY.withClickEvent(ComponentUtils.openUrlClickEvent( "https://xkcd.com/838/"))));
                                return 1;
                            }
                            var command = StringArgumentType.getString(context, "command");

                            context.getSource().sendSuccess(() -> Component.literal(String.format("Executing '%s' as Server", command)), true);

                            var commandOutput = DoAsCommand.getCommandOutput(context.getSource());

                            var server = context.getSource().getServer();
                            var source = buildServerSource(commandOutput, server);
                            execute(dispatcher, command, source, context.getSource());

                            return 1;
                        })
                );
    }

    public CommandSourceStack buildServerSource(CommandSource commandOutput, MinecraftServer server) {
        return new CommandSourceStack(
                commandOutput,
                //? if >= 26.1 {
                /*net.minecraft.world.phys.Vec3.atCenterOf(server.overworld().getRespawnData().pos()),
                *///? } elif >= 1.21.11 {
                /*server.overworld().getRespawnData().pos().getCenter(),
                *///? } else {
                server.overworld().getSharedSpawnPos().getCenter(),
                //? }
                Vec2.ZERO,
                server.overworld(),
                //? >= 1.21.11
                //net.minecraft.server.permissions.PermissionSet.ALL_PERMISSIONS,
                //? < 1.21.11
                4,
                "Server",
                Component.nullToEmpty("Server"),
                server,
                null
        );
    }
}
