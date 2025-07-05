package me.alexdevs.solstice.modules.back.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.modules.back.BackModule;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.literal;

public class BackCommand extends ModCommand<BackModule> {
    public BackCommand(BackModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("back");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(literal("force")
                        .executes(context -> execute(context, true))
                )
                .executes(context -> execute(context, false));
    }

    private int execute(CommandContext<CommandSourceStack> context, boolean force) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var playerContext = PlaceholderContext.of(player);

        var lastPosition = module.getPlayerLastLocation(player.getUUID());
        if (lastPosition == null) {
            context.getSource().sendSuccess(() -> module.locale().get(
                    "noPosition",
                    playerContext
            ), false);
            return 1;
        }

        context.getSource().sendSuccess(() -> module.locale().get(
                "teleporting",
                playerContext
        ), false);
        if (force) {
            lastPosition.teleport(player);
        } else {
            var range = module.getConfig().safeCheckRange;
            var success = lastPosition.safeTeleport(player, true, range);
            if (!success) {
                var placeholders = Map.of(
                        "forceBackButton", Components.button(
                                module.locale().raw("forceLabel"),
                                module.locale().raw("forceHover"),
                                "/back force"
                        )
                );

                context.getSource().sendSuccess(() -> module.locale().get(
                        "teleportFailed",
                        playerContext,
                        placeholders
                ), false);
            }
        }

        return 1;
    }

}
