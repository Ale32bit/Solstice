package me.alexdevs.solstice.commands.fun;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SmiteCommand {
    public static final EntityType<?> entityType = EntityType.LIGHTNING_BOLT;
    public static final int maxTimes = 1024;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("smite")
                .requires(Permissions.require("solstice.command.smite", 2))
                .then(argument("target", EntityArgumentType.players())
                        .executes(context ->
                                execute(context, 1)
                        )
                        .then(argument("times", IntegerArgumentType.integer(0, maxTimes))
                                .executes(context ->
                                        execute(context, IntegerArgumentType.getInteger(context, "times"))
                                )));


        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, int times) throws CommandSyntaxException {
        var targets = EntityArgumentType.getPlayers(context, "target");
        for (var i = 0; i < times; i++) {
            targets.forEach(target ->
                    entityType.create(
                            target.getServerWorld(),
                            (entity) -> target.getWorld().spawnEntity(entity),
                            target.getBlockPos(),
                            SpawnReason.COMMAND,
                            false,
                            false
                    )
//                    entityType.create(
//                            target.getServerWorld(),
//                            null,
//                            (entity) -> target.getWorld().spawnEntity(entity),
//                            target.getBlockPos(),
//                            SpawnReason.COMMAND,
//                            false,
//                            false)
            );
        }

        return targets.size();
    }
}
