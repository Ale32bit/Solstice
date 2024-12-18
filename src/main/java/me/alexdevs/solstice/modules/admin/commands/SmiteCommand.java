package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.util.Raycast;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SmiteCommand extends ModCommand {
    public static final EntityType<?> entityType = EntityType.LIGHTNING_BOLT;
    public static final int maxTimes = 1024;
    public static final int maxDistance = 512;

    public SmiteCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("smite");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(this::executePos)
                .then(argument("target", EntityArgumentType.entities())
                        .executes(context ->
                                execute(context, 1)
                        )
                        .then(argument("times", IntegerArgumentType.integer(0, maxTimes))
                                .executes(context ->
                                        execute(context, IntegerArgumentType.getInteger(context, "times"))
                                )));
    }

    private int executePos(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();

        var result = Raycast.cast(player, maxDistance);
        if(result.getType() == HitResult.Type.MISS) {
            return 0;
        }

        summon(player.getServerWorld(), result.getBlockPos().up());

        return 1;
    }

    private int execute(CommandContext<ServerCommandSource> context, int times) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var targets = EntityArgumentType.getEntities(context, "target");
        var timesToSummon = targets.size() * times;
        if (timesToSummon > maxTimes) {
            times = maxTimes / targets.size();
            if (times == 0)
                times = 1;
        }
        for (var i = 0; i < times; i++) {
            targets.forEach(target ->
                    summon(player.getServerWorld(), target.getBlockPos())
            );
        }

        return targets.size();
    }

    private void summon(ServerWorld world, BlockPos pos) {
        entityType.create(
                world,
                null,
                world::spawnEntity,
                pos,
                SpawnReason.COMMAND,
                false,
                false);
    }
}
