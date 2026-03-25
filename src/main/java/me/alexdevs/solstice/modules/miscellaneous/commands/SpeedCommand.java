package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;
import java.util.Map;

public class SpeedCommand extends ModCommand<MiscellaneousModule> {
    public SpeedCommand(MiscellaneousModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("speed");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("speed.base", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();

                    if (player.getAbilities().flying) {
                        return setFlyingSpeed(context, 1);
                    } else {
                        return setWalkingSpeed(context, 1);
                    }
                })
                .then(Commands.argument("speed", FloatArgumentType.floatArg(0))
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var speedMultiplier = FloatArgumentType.getFloat(context, "speed");
                            if (player.getAbilities().flying) {
                                return setFlyingSpeed(context, speedMultiplier);
                            } else {
                                return setWalkingSpeed(context, speedMultiplier);
                            }
                        }))

                .then(Commands.literal("walk")
                        .executes(context -> this.setWalkingSpeed(context, 1))
                        .then(Commands.argument("speed", FloatArgumentType.floatArg(0))
                                .executes(context -> this.setWalkingSpeed(context, FloatArgumentType.getFloat(context, "speed"))))
                )
                .then(Commands.literal("fly")
                        .executes(context -> this.setFlyingSpeed(context, 1))
                        .then(Commands.argument("speed", FloatArgumentType.floatArg(0))
                                .executes(context -> this.setFlyingSpeed(context, FloatArgumentType.getFloat(context, "speed"))))
                );
    }

    private int setWalkingSpeed(CommandContext<CommandSourceStack> context, float speedMultiplier) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var attributes = player.getAttributes();
        var instance = attributes.getInstance(Attributes.MOVEMENT_SPEED);
        var id = Solstice.ID.withPath("walking_speed_multiplier");

        var map = Map.of(
                "speed", Component.nullToEmpty(String.valueOf(speedMultiplier))
        );

        if (speedMultiplier == 1) {
            //? >= 1.21.1
            instance.removeModifier(id);
            //? < 1.21.1
            //instance.getModifiers().stream().filter(x -> x.getName().equals(id.toString())).findFirst().ifPresent(instance::removeModifier);

            context.getSource().sendSuccess(() -> module.locale().get("walkSpeedReset", map), true);
        } else {
            //? if >= 1.21.1 {
            var modifier = new AttributeModifier(id, speedMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            instance.addOrUpdateTransientModifier(modifier);
            //? } else {
            /*var res = instance.getModifiers().stream().filter(x -> x.getName().equals(id.toString())).findFirst();
            res.ifPresent(instance::removeModifier);

            var modifier = new AttributeModifier(id.toString(), speedMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL);
            instance.addTransientModifier(modifier);
            *///? }

            context.getSource().sendSuccess(() -> module.locale().get("walkSpeedSet", map), true);
        }

        return 1;
    }

    private int setFlyingSpeed(CommandContext<CommandSourceStack> context, float speedMultiplier) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var abilities = player.getAbilities();
        abilities.setFlyingSpeed(MiscellaneousModule.DEFAULT_FLY_SPEED * speedMultiplier);
        player.onUpdateAbilities();

        var map = Map.of(
                "speed", Component.nullToEmpty(String.valueOf(speedMultiplier))
        );

        if (speedMultiplier == 1) {
            context.getSource().sendSuccess(() -> module.locale().get("flySpeedReset", map), true);
        } else {
            context.getSource().sendSuccess(() -> module.locale().get("flySpeedSet", map), true);
        }

        return 1;
    }
}
