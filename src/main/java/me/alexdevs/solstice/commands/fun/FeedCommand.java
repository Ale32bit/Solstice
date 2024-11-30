package me.alexdevs.solstice.commands.fun;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FeedCommand {
    private static final int MAX_FOOD_LEVEL = 20;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("feed")
                .requires(Permissions.require("solstice.command.feed", 2))
                .executes(context -> execute(context, null))
                .then(argument("targets", EntityArgumentType.players())
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "targets"))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
        if (targets == null) {
            var player = context.getSource().getPlayerOrThrow();
            feed(context, player);
            return 1;
        } else {
            for (var target : targets) {
                feed(context, target);
            }

            return targets.size();
        }
    }

    private static void feed(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        player.getHungerManager().setFoodLevel(MAX_FOOD_LEVEL);
        player.getHungerManager().setSaturationLevel(MAX_FOOD_LEVEL);
        context.getSource().sendFeedback(() -> Text.literal("Fed ").append(player.getDisplayName()), true);
    }
}
