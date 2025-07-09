package me.alexdevs.solstice.modules.heal.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.heal.HealModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HealCommand extends ModCommand<HealModule> {
    public HealCommand(HealModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("heal");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("targets", EntityArgument.entities())
                        .requires(require("others", 2))
                        .executes(context -> execute(context, EntityArgument.getEntities(context, "targets"))));
    }

    private int execute(CommandContext<CommandSourceStack> context, @Nullable Collection<? extends Entity> targets) throws CommandSyntaxException {
        if (targets == null) {
            var player = context.getSource().getPlayerOrException();
            heal(context, player, 1);
            return 1;
        } else {
            var healedCount = 0;
            for (var target : targets) {
                if (target instanceof LivingEntity livingEntity) {
                    healedCount++;
                    heal(context, livingEntity, targets.size());
                }
            }

            if (healedCount == 0) {
                context.getSource().sendFailure(module.locale().get("noLiving"));
            } else if (healedCount > 1) {
                var placeholders = Map.of(
                        "count", Component.nullToEmpty(String.valueOf(healedCount))
                );

                context.getSource().sendSuccess(() -> module.locale().get("healedMultiple", placeholders), true);
            }
            return healedCount;
        }
    }

    private void heal(CommandContext<CommandSourceStack> context, LivingEntity entity, int count) {
        entity.setHealth(entity.getMaxHealth());

        if( count == 1) {
            var placeholders = Map.of(
                    "entity", entity.getName()
            );

            context.getSource().sendSuccess(() -> module.locale().get("healed", placeholders), true);
        }
    }
}
