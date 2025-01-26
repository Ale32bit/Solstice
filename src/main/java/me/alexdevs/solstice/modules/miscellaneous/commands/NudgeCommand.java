package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class NudgeCommand extends ModCommand<MiscellaneousModule> {
    public NudgeCommand(MiscellaneousModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("nudge");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("nudge.base", 2))
                .then(CommandManager.argument("entities", EntityArgumentType.entities())
                        .executes(context -> this.execute(context, 1, false))
                        .then(CommandManager.argument("power", FloatArgumentType.floatArg(0, 32))
                                .executes(context -> this.execute(context, FloatArgumentType.getFloat(context, "power"), false))
                                .then(CommandManager.argument("quiet", BoolArgumentType.bool())
                                        .executes(context -> this.execute(context, FloatArgumentType.getFloat(context, "power"), BoolArgumentType.getBool(context, "quiet")))
                                )
                        ));
    }

    private int execute(CommandContext<ServerCommandSource> context, float power, boolean quiet) throws CommandSyntaxException {
        var entities = EntityArgumentType.getEntities(context, "entities");

        var random = context.getSource().getServer().getOverworld().getRandom();

        for (var entity : entities) {
            if (entity instanceof LivingEntity living) {
                var angle = random.nextDouble() * Math.PI * 2;
                var x = Math.sin(angle);
                var z = Math.cos(angle);
                var vec = new Vec3d(x, 0, z).normalize().multiply(power);

                living.setVelocity(vec);
                living.velocityModified = true;
                if (!quiet) {
                    if (entity instanceof ServerPlayerEntity player) {
                        var pitch = (float) (Math.sin(random.nextDouble() * Math.PI * 2) / 10 + 1);
                        player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.MASTER, 1f, pitch);
                    }
                }
            }
        }

        return 1;
    }
}
