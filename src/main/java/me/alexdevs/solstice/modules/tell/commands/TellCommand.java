package me.alexdevs.solstice.modules.tell.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.integrations.VanishIntegration;
import me.alexdevs.solstice.modules.tell.TellModule;
import me.drex.vanish.api.VanishAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class TellCommand extends ModCommand<TellModule> {
    public TellCommand(TellModule module) {
        super(module);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        Utils.removeCommands(dispatcher, "msg", "tell", "w");
        super.register(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("tell", "msg", "w", "dm");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var playerManager = context.getSource().getServer().getPlayerList();
                            final String[] playerNamesArray;

                            if (VanishIntegration.isAvailable()) {
                                playerNamesArray = VanishIntegration.getVisiblePlayersAsString(context.getSource());
                            }
                            else {
                                playerNamesArray = playerManager.getPlayerNamesArray();
                            }

                            return SharedSuggestionProvider.suggest(
                                    playerNamesArray,
                                    builder);
                        })
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var targetName = StringArgumentType.getString(context, "player");
        var message = StringArgumentType.getString(context, "message");

        module.sendDirectMessage(targetName, source, message);
        return 1;
    }
}
