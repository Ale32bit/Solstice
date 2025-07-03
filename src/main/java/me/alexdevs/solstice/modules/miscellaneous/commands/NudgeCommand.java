package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("nudge.base", 2))
                .then(Commands.argument("entities", EntityArgument.entities())
                        .executes(context -> this.execute(context, 1, false))
                        .then(Commands.argument("power", FloatArgumentType.floatArg(0, 32))
                                .executes(context -> this.execute(context, FloatArgumentType.getFloat(context, "power"), false))
                                .then(Commands.argument("quiet", BoolArgumentType.bool())
                                        .executes(context -> this.execute(context, FloatArgumentType.getFloat(context, "power"), BoolArgumentType.getBool(context, "quiet")))
                                )
                        ));
    }

    private int execute(CommandContext<CommandSourceStack> context, float power, boolean quiet) throws CommandSyntaxException {
        var entities = EntityArgument.getEntities(context, "entities");

        var random = context.getSource().getServer().overworld().getRandom();

        for (var entity : entities) {
            if (entity instanceof LivingEntity living) {
                var angle = random.nextDouble() * Math.PI * 2;
                var x = Math.sin(angle);
                var z = Math.cos(angle);
                var vec = new Vec3(x, 0, z).normalize().scale(power);

                living.setDeltaMovement(vec);
                living.hurtMarked = true;
                if (!quiet) {
                    if (entity instanceof ServerPlayer player) {
                        var pitch = (float) (Math.sin(random.nextDouble() * Math.PI * 2) / 10 + 1);
                        player.playNotifySound(SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.MASTER, 1f, pitch);
                    }
                }
            }
        }

        return 1;
    }
}
