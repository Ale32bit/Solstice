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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("rocket.base", 2))
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .executes(context -> execute(context, ""))
                        .then(CommandManager.argument("flags", StringArgumentType.greedyString())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "flags")))
                        )
                );
    }

    private int execute(CommandContext<ServerCommandSource> context, String flags) throws CommandSyntaxException {
        var targets = EntityArgumentType.getEntities(context, "targets");

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
                var world = (ServerWorld) target.getWorld();
                var pos = target.getPos();
                DummyExplosion.spawn(world, pos, power * 2);
                world.playSound(null,
                        pos.x, pos.y, pos.z,
                        SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER,
                        2, 1);
            }

            target.addVelocity(0, power, 0);
            target.velocityModified = true;
        }


        return count;
    }
}
