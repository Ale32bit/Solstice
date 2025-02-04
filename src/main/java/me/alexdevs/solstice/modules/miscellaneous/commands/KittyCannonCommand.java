package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.DummyExplosion;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class KittyCannonCommand extends ModCommand<MiscellaneousModule> {
    public KittyCannonCommand(MiscellaneousModule module) {
        super(module);
    }

    public static final EntityType<?> BALL = EntityType.CAT;

    @Override
    public List<String> getNames() {
        return List.of("kittycannon");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("kittycannon.base", 2))
                .executes(context -> {
                    final var player = context.getSource().getPlayerOrThrow();

                    final var world = player.getServerWorld();

                    BALL.create(world, entity -> {
                        entity.setVelocity(player.getRotationVector().multiply(3.5));
                        entity.setPosition(player.getEyePos().add(player.getRotationVector()));
                        world.spawnEntity(entity);

                        Solstice.scheduler.scheduleSync(() -> {
                            final var pos = entity.getPos();
                            DummyExplosion.spawn(world, pos, 0);
                            entity.remove(Entity.RemovalReason.DISCARDED);
                        }, 1, TimeUnit.SECONDS);
                    }, player.getBlockPos().up(), SpawnReason.COMMAND, true, false);


                    return 1;
                });
    }
}
