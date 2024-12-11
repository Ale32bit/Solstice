package me.alexdevs.solstice.modules.admin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HealCommand extends ModCommand {
    public HealCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("heal");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("targets", EntityArgumentType.entities())
                        .executes(context -> execute(context, EntityArgumentType.getEntities(context, "targets"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable Collection<? extends Entity> targets) throws CommandSyntaxException {
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

            if (healedCount == 0) {
                context.getSource().sendError(Text.of("There are no living entities in the selector"));
            }
            return healedCount;
        }
    }

    private void heal(CommandContext<ServerCommandSource> context, LivingEntity entity) {
        entity.setHealth(entity.getMaxHealth());
        context.getSource().sendFeedback(() -> Text.literal("Healed ").append(entity.getDisplayName()), true);
    }
}
