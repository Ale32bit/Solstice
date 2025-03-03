package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FirstSpawnCommand extends ModCommand<SpawnModule> {
    public FirstSpawnCommand(SpawnModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("firstspawn");
    }

    private int execute(CommandContext<CommandSourceStack> context, @Nullable Collection<ServerPlayer> players) throws CommandSyntaxException {
        if (module.getFirstSpawn() == null) {
            context.getSource().sendSuccess(() -> module.locale().get("noFirstSpawn"), false);
            return 0;
        }
        if (players == null) {
            var player = context.getSource().getPlayerOrException();
            sendToFirstSpawn(context, player);
            return 1;
        } else {
            for (ServerPlayer player : players) {
                sendToFirstSpawn(context, player);
                context.getSource().sendSuccess(() -> Component.literal("Sent ").append(player.getDisplayName()).append(" to first spawn."), true);
            }
            return players.size();
        }
    }

    private void sendToFirstSpawn(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        var playerContext = PlaceholderContext.of(player);
        context.getSource().sendSuccess(() -> module.locale().get(
                "teleporting",
                playerContext
        ), false);

        module.getFirstSpawn().teleport(player);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("firstspawn", true))
                .executes(context -> execute(context, null))
                .then(argument("players", EntityArgument.players())
                        .executes(context -> execute(context, EntityArgument.getPlayers(context, "players"))));
    }


}
