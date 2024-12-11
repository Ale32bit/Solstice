package me.alexdevs.solstice.modules.near.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.near.NearModule;
import me.alexdevs.solstice.modules.near.data.NearConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NearCommand extends ModCommand {
    private final Locale locale = Solstice.localeManager.getLocale(NearModule.ID);

    public NearCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("near");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, Solstice.configManager.getData(NearConfig.class).defaultRange, context.getSource().getPlayerOrThrow()))
                .then(argument("range", IntegerArgumentType.integer(0, Solstice.configManager.getData(NearConfig.class).maxRange))
                        .executes(context -> execute(context, IntegerArgumentType.getInteger(context, "range"), context.getSource().getPlayerOrThrow())));
    }

    private int execute(CommandContext<ServerCommandSource> context, int range, ServerPlayerEntity sourcePlayer) {
        var playerContext = PlaceholderContext.of(sourcePlayer);
        var list = new ArrayList<ClosePlayers>();

        var sourcePos = sourcePlayer.getPos();
        sourcePlayer.getServerWorld().getPlayers().forEach(targetPlayer -> {
            var targetPos = targetPlayer.getPos();
            if (!sourcePlayer.getUuid().equals(targetPlayer.getUuid()) && sourcePos.isInRange(targetPos, range)) {
                var distance = sourcePos.distanceTo(targetPos);
                list.add(new ClosePlayers(targetPlayer, distance));
            }
        });

        if (list.isEmpty()) {
            context.getSource().sendFeedback(() -> locale.get(
                    "noOne",
                    playerContext
            ), false);
            return 1;
        }

        list.sort(Comparator.comparingDouble(ClosePlayers::distance));

        var listText = Text.empty();
        var comma = locale.get("comma");
        for (int i = 0; i < list.size(); i++) {
            var player = list.get(i);
            if (i > 0) {
                listText = listText.append(comma);
            }
            var placeholders = Map.of(
                    "player", player.player.getDisplayName(),
                    "distance", Text.of(String.format("%.1fm", player.distance))
            );

            var targetContext = PlaceholderContext.of(sourcePlayer);

            listText = listText.append(locale.get(
                    "format",
                    targetContext,
                    placeholders
            ));
        }

        var placeholders = Map.of(
                "playerList", (Text) listText
        );
        context.getSource().sendFeedback(() -> locale.get(
                "nearestPlayers",
                playerContext,
                placeholders
        ), false);

        return 1;
    }

    private record ClosePlayers(ServerPlayerEntity player, double distance) {
    }
}
