package me.alexdevs.solstice.modules.back.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.back.BackModule;
import net.minecraft.commands.CommandSourceStack;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var playerContext = PlaceholderContext.of(player);

                    var lastPosition = module.getPlayerLastLocation(player.getUUID());
                    if (lastPosition == null) {
                        context.getSource().sendSuccess(() -> locale.get(
                                "noPosition",
                                playerContext
                        ), false);
                        return 1;
                    }

                    context.getSource().sendSuccess(() -> locale.get(
                            "teleporting",
                            playerContext
                    ), false);
                    lastPosition.teleport(player);

                    return 1;
                });
    }
}
