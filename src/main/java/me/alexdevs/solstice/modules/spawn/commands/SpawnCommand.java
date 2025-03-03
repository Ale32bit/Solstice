package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SpawnCommand extends ModCommand<SpawnModule> {
    public SpawnCommand(SpawnModule module) {
        super(module);
    }

    private int execute(CommandContext<CommandSourceStack> context, @Nullable ServerLevel world, @Nullable Collection<ServerPlayer> players) throws CommandSyntaxException {
        var config = module.getConfig();
        var skipPermCheck = false;
        if (world == null) {
            if (config.globalSpawn.onSpawnCommand) {
                world = module.getGlobalSpawnWorld();
                skipPermCheck = true;
            } else {
                world = context.getSource().getLevel();
            }
        }

        var worldName = world.dimension().location().toString();

        if(config.requireWorldPermission && !skipPermCheck) {
            if (!Permissions.check(context.getSource(), getPermissionNode("worlds." + worldName), 2)) {
                context.getSource().sendSuccess(() -> module.locale().get("noWorldPermission", Map.of("world", Component.nullToEmpty(worldName))), false);
                return 0;
            }
        }

        if (players == null) {
            var player = context.getSource().getPlayerOrException();
            sendToSpawn(context, player, world);
            return 1;
        } else {
            for (ServerPlayer player : players) {
                sendToSpawn(context, player, world);
                context.getSource().sendSuccess(() -> Component.literal("Sent ").append(player.getDisplayName()).append(" to " + worldName + " spawn."), true);
            }
            return players.size();
        }
    }

    private void sendToSpawn(CommandContext<CommandSourceStack> context, ServerPlayer player, ServerLevel world) {
        var playerContext = PlaceholderContext.of(player);
        context.getSource().sendSuccess(() -> module.locale().get(
                "teleporting",
                playerContext
        ), false);

        module.sendToSpawn(player, world);
    }

    @Override
    public List<String> getNames() {
        return List.of("spawn");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> execute(context, null, null))
                .then(argument("world", DimensionArgument.dimension())
                        .requires(require("worlds.base", true))
                        .executes(context -> execute(context, DimensionArgument.getDimension(context, "world"), null))
                        .then(argument("players", EntityArgument.players())
                                .requires(require("others", 2))
                                .executes(context -> execute(context, DimensionArgument.getDimension(context, "world"), EntityArgument.getPlayers(context, "players"))))
                );
    }
}
