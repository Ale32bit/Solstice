package me.alexdevs.solstice.modules.info.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.info.InfoModule;
import net.minecraft.commands.CommandSourceStack;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class RulesCommand extends ModCommand<InfoModule> {
    public RulesCommand(InfoModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("rules");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("rules", true))
                .executes(context -> {
                    var sourceContext = PlaceholderContext.of(context.getSource());
                    var rules = module.getPage("rules", sourceContext);
                    context.getSource().sendSuccess(() -> rules, false);
                    return 1;
                });
    }
}
