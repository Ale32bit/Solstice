package me.alexdevs.solstice.modules.near.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.near.NearModule;
import me.alexdevs.solstice.modules.near.data.NearConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class NearCommand extends ModCommand<NearModule> {
    public NearCommand(NearModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("near");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, Solstice.configManager.getData(NearConfig.class).defaultRange, context.getSource().getPlayerOrException()))
                .then(argument("range", IntegerArgumentType.integer(0, Solstice.configManager.getData(NearConfig.class).maxRange))
                        .executes(context -> execute(context, IntegerArgumentType.getInteger(context, "range"), context.getSource().getPlayerOrException())));
    }

    private int execute(CommandContext<CommandSourceStack> context, int range, ServerPlayer sourcePlayer) {
        var playerContext = PlaceholderContext.of(sourcePlayer);
        var list = new ArrayList<ClosePlayers>();

        var sourcePos = sourcePlayer.position();
        sourcePlayer.serverLevel().players().forEach(targetPlayer -> {
            var targetPos = targetPlayer.position();
            if (!sourcePlayer.getUUID().equals(targetPlayer.getUUID()) && sourcePos.closerThan(targetPos, range)) {
                var distance = sourcePos.distanceTo(targetPos);
                list.add(new ClosePlayers(targetPlayer, distance));
            }
        });

        if (list.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get(
                    "noOne",
                    playerContext
            ), false);
            return 1;
        }

        list.sort(Comparator.comparingDouble(ClosePlayers::distance));

        var listText = Component.empty();
        var comma = module.locale().get("comma");
        for (int i = 0; i < list.size(); i++) {
            var player = list.get(i);
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "player", player.player.getDisplayName(),
                    "distance", Component.nullToEmpty(String.format("%.1fm", player.distance))
            );

            var targetContext = PlaceholderContext.of(sourcePlayer);

            listText = listText.append(module.locale().get(
                    "format",
                    targetContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "playerList", (Component) listText
        );
        context.getSource().sendSuccess(() -> module.locale().get(
                "nearestPlayers",
                playerContext,
                placeholders
        ), false);

        return 1;
    }

    private record ClosePlayers(ServerPlayer player, double distance) {
    }
}
