package me.alexdevs.solstice.modules.feed.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.feed.FeedModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("targets", EntityArgument.players())
                        .requires(require("others", 2))
                        .executes(context -> execute(context, EntityArgument.getPlayers(context, "targets"))));
    }

    private int execute(CommandContext<CommandSourceStack> context, @Nullable Collection<ServerPlayer> targets) throws CommandSyntaxException {
        if (targets == null) {
            var player = context.getSource().getPlayerOrException();
            feed(context, player);
            return 1;
        } else {
            for (var target : targets) {
                feed(context, target);
            }

            return targets.size();
        }
    }

    private void feed(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        player.getFoodData().setFoodLevel(MAX_FOOD_LEVEL);
        player.getFoodData().setSaturation(MAX_FOOD_LEVEL);
        var placeholders = Map.of(
                "entity", player.getName()
        );
        context.getSource().sendSuccess(() -> module.locale().get("fed", placeholders), true);
    }
}
