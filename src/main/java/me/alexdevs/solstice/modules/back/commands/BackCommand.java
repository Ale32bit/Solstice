package me.alexdevs.solstice.modules.back.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.back.BackModule;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class BackCommand extends ModCommand<BackModule> {
    private final Locale locale = Solstice.localeManager.getLocale(BackModule.ID);

    public BackCommand(BackModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("back");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var playerContext = PlaceholderContext.of(player);

                    var lastPosition = module.lastPlayerPositions.get(player.getUuid());
                    if (lastPosition == null) {
                        context.getSource().sendFeedback(() -> locale.get(
                                "noPosition",
                                playerContext
                        ), false);
                        return 1;
                    }

                    context.getSource().sendFeedback(() -> locale.get(
                            "teleporting",
                            playerContext
                    ), false);
                    lastPosition.teleport(player);

                    return 1;
                });
    }
}
