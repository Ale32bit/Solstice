package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.Flags;
import me.alexdevs.solstice.api.command.flags.Flag;
import me.alexdevs.solstice.api.command.flags.FloatFlag;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.DummyExplosion;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class RocketCommand extends ModCommand<MiscellaneousModule> {
    public RocketCommand(MiscellaneousModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("rocket");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("rocket.base", 2))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(context -> execute(context, ""))
                        .then(Commands.argument("flags", StringArgumentType.greedyString())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "flags")))
                        )
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, String flags) throws CommandSyntaxException {
        var targets = EntityArgument.getEntities(context, "targets");

        var explodeFlag = new Flag("explode", List.of('e'));
        var powerFlag = new FloatFlag("power", List.of('p'));
        Flags.parse(flags, explodeFlag, powerFlag);

        var explode = explodeFlag.isUsed();
        var power = 2.0f;
        if (powerFlag.isUsed()) {
            power = powerFlag.getValue();
        }

        var count = 0;
        for (var target : targets) {
            count++;
            if (explode) {
                var world = (ServerLevel) target.level();
                var pos = target.position();
                DummyExplosion.spawn(world, pos, power * 2);
                world.playSound(null,
                        pos.x, pos.y, pos.z,
                        SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.MASTER,
                        2, 1);
            }

            target.push(0, power, 0);
            target.hurtMarked = true;
        }


        return count;
    }
}
