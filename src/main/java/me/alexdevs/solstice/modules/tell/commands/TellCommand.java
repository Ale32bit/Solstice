package me.alexdevs.solstice.modules.tell.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.modules.tell.TellModule;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class TellCommand extends ModCommand<TellModule> {
    public TellCommand(TellModule module) {
        super(module);
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        Utils.removeCommands(dispatcher, "msg", "tell", "w");
        super.register(dispatcher, commandRegistry, environment);
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

        module.sendDirectMessage(targetName, source, message);
        return 1;
    }
}
