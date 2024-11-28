package me.alexdevs.solstice.commands.teleport;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportHereCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("tphere")
                .requires(Permissions.require("solstice.command.tphere", 2))
                .then(argument("targets", EntityArgumentType.entities())
                        .executes(context -> {
                            var source = context.getSource();
                            var player = source.getPlayerOrThrow();
                            var world = player.getServerWorld();
                            var vec3d = player.getPos();
                            var yaw = player.getYaw();
                            var pitch = player.getPitch();

                            var targets = EntityArgumentType.getEntities(context, "targets");

                            targets.forEach(target -> {
                                target.teleport(world, vec3d.x, vec3d.y, vec3d.z, Set.of(), yaw, pitch);
                                target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
                                target.setOnGround(true);

                                if (target instanceof PathAwareEntity pathAwareEntity) {
                                    pathAwareEntity.getNavigation().stop();
                                }
                            });

                            if (targets.size() == 1) {
                                source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.single", targets.iterator().next().getDisplayName(), player.getDisplayName()), true);
                            } else {
                                source.sendFeedback(() -> Text.translatable("commands.teleport.success.entity.multiple", targets.size(), player.getDisplayName()), true);
                            }

                            return targets.size();
                        }));

        dispatcher.register(rootCommand);
    }
}
