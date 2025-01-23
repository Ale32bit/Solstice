package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("sleep.base", 1))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    module.putToSleep(player);
                    return 1;
                })
                .then(CommandManager.argument("entities", EntityArgumentType.entities())
                        .requires(require("sleep.others", 2))
                        .executes(context -> {
                            var targets = EntityArgumentType.getEntities(context, "entities");
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
