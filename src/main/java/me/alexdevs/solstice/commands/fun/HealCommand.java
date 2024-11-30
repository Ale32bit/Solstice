package me.alexdevs.solstice.commands.fun;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HealCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("heal")
                .requires(Permissions.require("solstice.command.heal", 2))
                .executes(context -> execute(context, null))
                .then(argument("targets", EntityArgumentType.entities())
                        .executes(context -> execute(context, EntityArgumentType.getEntities(context, "targets"))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<? extends Entity> targets) throws CommandSyntaxException {
        if (targets == null) {
            var player = context.getSource().getPlayerOrThrow();
            heal(context, player);
            return 1;
        } else {
            var healedCount = 0;
            for (var target : targets) {
                if (target instanceof LivingEntity livingEntity) {
                    healedCount++;
                    heal(context, livingEntity);
                }
            }

            if(healedCount == 0) {
                context.getSource().sendError(Text.of("There are no living entities in the selector"));
            }
            return healedCount;
        }
    }

    private static void heal(CommandContext<ServerCommandSource> context, LivingEntity entity) {
        entity.setHealth(entity.getMaxHealth());
        context.getSource().sendFeedback(() -> Text.literal("Healed ").append(entity.getDisplayName()), true);
    }
}
