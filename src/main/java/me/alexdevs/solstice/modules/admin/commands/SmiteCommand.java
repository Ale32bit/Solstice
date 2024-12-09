package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SmiteCommand extends ModCommand {
    public static final EntityType<?> entityType = EntityType.LIGHTNING_BOLT;
    public static final int maxTimes = 1024;

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
                .then(argument("target", EntityArgumentType.players())
                        .executes(context ->
                                execute(context, 1)
                        )
                        .then(argument("times", IntegerArgumentType.integer(0, maxTimes))
                                .executes(context ->
                                        execute(context, IntegerArgumentType.getInteger(context, "times"))
                                )));
    }

    private int execute(CommandContext<ServerCommandSource> context, int times) throws CommandSyntaxException {
        var targets = EntityArgumentType.getPlayers(context, "target");
        for (var i = 0; i < times; i++) {
            targets.forEach(target ->
                    entityType.create(
                            target.getServerWorld(),
                            null,
                            (entity) -> target.getWorld().spawnEntity(entity),
                            target.getBlockPos(),
                            SpawnReason.COMMAND,
                            false,
                            false)
            );
        }

        return targets.size();
    }
}
