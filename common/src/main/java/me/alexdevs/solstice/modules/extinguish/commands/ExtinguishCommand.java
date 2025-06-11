package me.alexdevs.solstice.modules.extinguish.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.extinguish.ExtinguishModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ExtinguishCommand extends ModCommand<ExtinguishModule> {
    public ExtinguishCommand(ExtinguishModule module) {
        super(module);
    }

    private static int execute(CommandContext<CommandSourceStack> context, @Nullable Collection<ServerPlayer> players) throws CommandSyntaxException {
        var source = context.getSource();
        if (players == null) {
            extinguish(source, source.getPlayerOrException());
            return 1;
        } else {
            for (ServerPlayer player : players) {
                extinguish(source, player);
            }

            return players.size();
        }
    }

    private static void extinguish(CommandSourceStack source, ServerPlayer player) {
        player.clearFire();
        source.sendSuccess(() -> Component.literal("Extinguished ").append(source.getDisplayName()), true);
    }

    @Override
    public List<String> getNames() {
        return List.of("extinguish", "ex");
    }

    public LiteralArgumentBuilder<CommandSourceStack> command(String command) {
        return literal(command)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("players", EntityArgument.players())
                        .executes(context -> execute(context, EntityArgument.getPlayers(context, "players"))));
    }
}
