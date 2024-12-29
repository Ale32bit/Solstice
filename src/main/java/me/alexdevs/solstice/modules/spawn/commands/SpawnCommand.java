package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand extends ModCommand<SpawnModule> {
    public SpawnCommand(SpawnModule module) {
        super(module);
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        if (players == null) {
            var player = context.getSource().getPlayerOrThrow();
            sendToSpawn(context, player);
            return 1;
        } else {
            for (ServerPlayerEntity player : players) {
                sendToSpawn(context, player);
                context.getSource().sendFeedback(() -> Text.literal("Sent ").append(player.getDisplayName()).append(" to spawn."), true);
            }
            return players.size();
        }
    }

    private void sendToSpawn(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        var playerContext = PlaceholderContext.of(player);
        context.getSource().sendFeedback(() -> module.locale().get(
                "teleporting",
                playerContext
        ), false);

        module.sendToSpawn(player);
    }

    @Override
    public List<String> getNames() {
        return List.of("spawn");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> execute(context, null))
                .then(argument("players", EntityArgumentType.players())
                        .requires(require("others", 2))
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "players"))));
    }
}
