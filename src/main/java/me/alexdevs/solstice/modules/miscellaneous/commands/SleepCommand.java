package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class SleepCommand extends ModCommand<MiscellaneousModule> {
    public SleepCommand(MiscellaneousModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("sleep");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("sleep.base", 1))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    module.putToSleep(player);
                    return 1;
                })
                .then(Commands.argument("entities", EntityArgument.entities())
                        .requires(require("sleep.others", 2))
                        .executes(context -> {
                            var targets = EntityArgument.getEntities(context, "entities");
                            var count = 0;
                            for (var target : targets) {
                                if (target instanceof LivingEntity entity) {
                                    module.putToSleep(entity);
                                    count++;
                                }
                            }
                            return count;
                        })
                );
    }
}
