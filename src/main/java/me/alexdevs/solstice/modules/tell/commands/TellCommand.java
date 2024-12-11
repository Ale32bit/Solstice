package me.alexdevs.solstice.modules.tell.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.Utils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class TellCommand extends ModCommand {
    public TellCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public void register() {
        Utils.removeCommands(dispatcher, "msg", "tell", "w");
        super.register();
    }

    @Override
    public List<String> getNames() {
        return List.of("tell", "msg", "w", "dm");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var playerManager = context.getSource().getServer().getPlayerManager();
                            return CommandSource.suggestMatching(
                                    playerManager.getPlayerNames(),
                                    builder);
                        })
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(this::execute)));
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        var targetName = StringArgumentType.getString(context, "player");
        var message = StringArgumentType.getString(context, "message");

        Solstice.modules.tell.sendDirectMessage(targetName, source, message);
        return 1;
    }
}
