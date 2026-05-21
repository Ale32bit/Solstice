package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.EntityUtils;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.miscellaneous.DummyExplosion;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("kittycannon.base", 2))
                .executes(context -> {
                    final var player = context.getSource().getPlayerOrException();

                    final var world = PlayerUtils.getLevel(player);

                    EntityUtils.createWithCommand(BALL, world, entity -> {
                        entity.setDeltaMovement(player.getLookAngle().scale(3.5));
                        entity.setPos(player.getEyePosition().add(player.getLookAngle()));
                        world.addFreshEntity(entity);

                        Solstice.scheduler.scheduleSync(() -> {
                            final var pos = entity.position();
                            DummyExplosion.spawn(world, pos, 0);
                            entity.remove(Entity.RemovalReason.DISCARDED);
                        }, 1, TimeUnit.SECONDS);
                    }, player.blockPosition().above(), true, false);


                    return 1;
                });
    }
}
