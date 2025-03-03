package me.alexdevs.solstice.modules.ignite.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.ignite.IgniteModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class IgniteCommand extends ModCommand<IgniteModule> {
    public static final int defaultTicks = 200; // 10 seconds

    public IgniteCommand(IgniteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("ignite");
    }

    public LiteralArgumentBuilder<CommandSourceStack> command(String command) {
        return literal(command)
                .requires(require(2))
                .executes(context -> execute(context, null, null))
                .then(argument("players", EntityArgument.players())
                        .executes(context -> execute(context, EntityArgument.getPlayers(context, "players"), null))
                        .then(argument("ticks", IntegerArgumentType.integer(0))
                                .executes(context ->
                                        execute(context, EntityArgument.getPlayers(context, "players"), IntegerArgumentType.getInteger(context, "ticks"))
                                )
                        )
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, @Nullable Collection<ServerPlayer> players, @Nullable Integer ticks) throws CommandSyntaxException {
        var source = context.getSource();
        if (players == null) {
            ignite(source, source.getPlayerOrException(), ticks);
            return 1;
        } else {
            for (ServerPlayer player : players) {
                ignite(source, player, ticks);
            }

            return players.size();
        }
    }

    private void ignite(CommandSourceStack source, ServerPlayer player, @Nullable Integer ticks) {
        if (ticks == null) {
            player.setRemainingFireTicks(defaultTicks);
            source.sendSuccess(() -> Component.literal("Ignited ").append(source.getDisplayName()), true);
        } else {
            player.setRemainingFireTicks(ticks);
            source.sendSuccess(() -> Component.literal("Ignited ").append(source.getDisplayName()).append(Component.nullToEmpty(String.format(" for %d ticks", ticks))), true);
        }
    }
}
