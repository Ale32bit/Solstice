package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand extends ModCommand<SpawnModule> {
    public SpawnCommand(SpawnModule module) {
        super(module);
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable ServerWorld world, @Nullable Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        var config = module.getConfig();
        var skipPermCheck = false;
        if (world == null) {
            if (config.globalSpawn.onSpawnCommand) {
                world = module.getGlobalSpawnWorld();
                skipPermCheck = true;
            } else {
                world = context.getSource().getWorld();
            }
        }

        var worldName = world.getRegistryKey().getValue().toString();

        if(config.requireWorldPermission && !skipPermCheck) {
            if (!Permissions.check(context.getSource(), getPermissionNode("worlds." + worldName), 2)) {
                context.getSource().sendFeedback(() -> module.locale().get("noWorldPermission", Map.of("world", Text.of(worldName))), false);
                return 0;
            }
        }

        if (players == null) {
            var player = context.getSource().getPlayerOrThrow();
            sendToSpawn(context, player, world);
            return 1;
        } else {
            for (ServerPlayerEntity player : players) {
                sendToSpawn(context, player, world);
                context.getSource().sendFeedback(() -> Text.literal("Sent ").append(player.getDisplayName()).append(" to " + worldName + " spawn."), true);
            }
            return players.size();
        }
    }

    private void sendToSpawn(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, ServerWorld world) {
        var playerContext = PlaceholderContext.of(player);
        context.getSource().sendFeedback(() -> module.locale().get(
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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> execute(context, null, null))
                .then(argument("world", DimensionArgumentType.dimension())
                        .requires(require("worlds.base", true))
                        .executes(context -> execute(context, DimensionArgumentType.getDimensionArgument(context, "world"), null))
                        .then(argument("players", EntityArgumentType.players())
                                .requires(require("others", 2))
                                .executes(context -> execute(context, DimensionArgumentType.getDimensionArgument(context, "world"), EntityArgumentType.getPlayers(context, "players"))))
                );
    }
}
