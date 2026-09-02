package me.alexdevs.solstice.modules.experiments.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class LengthCommand extends ModCommand<ExperimentsModule> {
    public LengthCommand(ExperimentsModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("length");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(literal("height")
                        .executes(context -> {
                            for (int i = 100; i > 0; i--) {
                                final var j = i; // this is utterly stupid
                                context.getSource().sendSuccess(() -> Component.literal("Line: " + j), false);
                            }
                            return 1;
                        }))
                .then(literal("width")
                        .executes(context -> {
                            var line = Component.empty();
                            var line2 = Component.empty();
                            for (int i = 0; i < 53; i++) { // should be a full line (54) in default client config, minus one just because...
                                var j = (i + 1) % 10;
                                line = line.append(Component.literal(j + ""));
                                line2 = line2.append(Component.literal("="));
                            }

                            final var finalLine = line;
                            final var finalLine2 = line2;
                            context.getSource().sendSuccess(() -> finalLine, false);
                            context.getSource().sendSuccess(() -> finalLine2, false);
                            return 1;
                        }));
    }
}
