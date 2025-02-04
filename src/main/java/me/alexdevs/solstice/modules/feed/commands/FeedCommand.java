package me.alexdevs.solstice.modules.feed.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.feed.FeedModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FeedCommand extends ModCommand<FeedModule> {
    private static final int MAX_FOOD_LEVEL = 20;

    public FeedCommand(FeedModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("feed");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("targets", EntityArgumentType.players())
                        .requires(require("others", 2))
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "targets"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
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

    private void feed(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        player.getHungerManager().setFoodLevel(MAX_FOOD_LEVEL);
        player.getHungerManager().setSaturationLevel(MAX_FOOD_LEVEL);
        context.getSource().sendFeedback(() -> Text.literal("Fed ").append(player.getDisplayName()), true);
    }
}
