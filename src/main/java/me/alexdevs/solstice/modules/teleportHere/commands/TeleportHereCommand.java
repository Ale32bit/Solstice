package me.alexdevs.solstice.modules.teleportHere.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.teleportHere.TeleportHereModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.PathfinderMob;

import java.util.List;
import java.util.Set;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeleportHereCommand extends ModCommand<TeleportHereModule> {
    public TeleportHereCommand(TeleportHereModule module) {
        super(module);
    }

    public List<String> getNames() {
        return List.of("tphere");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("targets", EntityArgument.entities())
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrException();
                            var world = player.serverLevel();
                            var vec3d = player.position();
                            var yaw = player.getYRot();
                            var pitch = player.getXRot();

                            var targets = EntityArgument.getEntities(context, "targets");

                            targets.forEach(target -> {
                                //? if >= 1.21.4 {
                                /*target.teleportTo(world, vec3d.x, vec3d.y, vec3d.z, Set.of(), yaw, pitch, false);
                                *///? } else {
                                target.teleportTo(world, vec3d.x, vec3d.y, vec3d.z, Set.of(), yaw, pitch);
                                //? }
                                target.setDeltaMovement(target.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                                target.setOnGround(true);

                                if (target instanceof PathfinderMob pathAwareEntity) {
                                    pathAwareEntity.getNavigation().stop();
                                }
                            });

                            if (targets.size() == 1) {
                                source.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.single", targets.iterator().next().getDisplayName(), player.getDisplayName()), true);
                            } else {
                                source.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.multiple", targets.size(), player.getDisplayName()), true);
                            }

                            return targets.size();
                        }));
    }
}
