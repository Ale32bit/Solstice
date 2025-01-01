package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FirstSpawnCommand extends ModCommand<SpawnModule> {
    public FirstSpawnCommand(SpawnModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("firstspawn");
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        if (module.getFirstSpawn() == null) {
            context.getSource().sendFeedback(() -> module.locale().get("noFirstSpawn"), false);
            return 0;
        }
        if (players == null) {
            var player = context.getSource().getPlayerOrThrow();
            sendToFirstSpawn(context, player);
            return 1;
        } else {
            for (ServerPlayerEntity player : players) {
                sendToFirstSpawn(context, player);
                context.getSource().sendFeedback(() -> Text.literal("Sent ").append(player.getDisplayName()).append(" to first spawn."), true);
            }
            return players.size();
        }
    }

    private void sendToFirstSpawn(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        var playerContext = PlaceholderContext.of(player);
        context.getSource().sendFeedback(() -> module.locale().get(
                "teleporting",
                playerContext
        ), false);

        module.getFirstSpawn().teleport(player);
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("firstspawn", true))
                .executes(context -> execute(context, null))
                .then(argument("players", EntityArgumentType.players())
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "players"))));
    }


}
