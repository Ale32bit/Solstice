package me.alexdevs.solstice.modules.smite.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.Raycast;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.smite.SmiteModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.HitResult;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SmiteCommand extends ModCommand<SmiteModule> {
    public static final EntityType<?> entityType = EntityType.LIGHTNING_BOLT;
    public static final int maxTimes = 1024;
    public static final int maxDistance = 512;

    public SmiteCommand(SmiteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("smite");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(this::executePos)
                .then(argument("target", EntityArgument.entities())
                        .executes(context ->
                                execute(context, 1)
                        )
                        .then(argument("times", IntegerArgumentType.integer(0, maxTimes))
                                .executes(context ->
                                        execute(context, IntegerArgumentType.getInteger(context, "times"))
                                )));
    }

    private int executePos(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var result = Raycast.cast(player, maxDistance);
        if (result.getType() == HitResult.Type.MISS) {
            return 0;
        }

        summon(player.serverLevel(), result.getBlockPos().above());

        return 1;
    }

    private int execute(CommandContext<CommandSourceStack> context, int times) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var targets = EntityArgument.getEntities(context, "target");
        var timesToSummon = targets.size() * times;
        if (timesToSummon > maxTimes) {
            times = maxTimes / targets.size();
            if (times == 0)
                times = 1;
        }
        for (var i = 0; i < times; i++) {
            targets.forEach(target ->
                    summon(player.serverLevel(), target.blockPosition())
            );
        }

        return targets.size();
    }

    private void summon(ServerLevel world, BlockPos pos) {
        entityType.create(
                world,
                null,
                world::addFreshEntity,
                pos,
                MobSpawnType.COMMAND,
                false,
                false);
    }
}
