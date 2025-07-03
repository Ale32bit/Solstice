package me.alexdevs.solstice.modules.afk.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.afk.AfkModule;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class AfkCommand extends ModCommand<AfkModule> {
    public AfkCommand(AfkModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("afk");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();

                    module.setPlayerAfk(player, !module.isPlayerAfk(player));

                    return 1;
                });
    }
}
