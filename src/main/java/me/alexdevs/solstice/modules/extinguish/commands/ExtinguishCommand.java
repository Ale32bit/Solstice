package me.alexdevs.solstice.modules.extinguish.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.extinguish.ExtinguishModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExtinguishCommand extends ModCommand<ExtinguishModule> {
    public ExtinguishCommand(ExtinguishModule module) {
        super(module);
    }

    private static int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        var source = context.getSource();
        if (players == null) {
            extinguish(source, source.getPlayerOrThrow());
            return 1;
        } else {
            for (ServerPlayerEntity player : players) {
                extinguish(source, player);
            }

            return players.size();
        }
    }

    private static void extinguish(ServerCommandSource source, ServerPlayerEntity player) {
        player.extinguish();
        source.sendFeedback(() -> Text.literal("Extinguished ").append(source.getDisplayName()), true);
    }

    @Override
    public List<String> getNames() {
        return List.of("extinguish", "ex");
    }

    public LiteralArgumentBuilder<ServerCommandSource> command(String command) {
        return literal(command)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("players", EntityArgumentType.players())
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "players"))));
    }
}
