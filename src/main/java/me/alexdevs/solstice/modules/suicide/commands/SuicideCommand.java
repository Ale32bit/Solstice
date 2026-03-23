package me.alexdevs.solstice.modules.suicide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.suicide.SuicideModule;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class SuicideCommand extends ModCommand<SuicideModule> {
    public SuicideCommand(SuicideModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("suicide");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();

                    //? if >= 1.21.4 {
                    /*player.kill(player.serverLevel());
                    *///? } else {
                    player.kill();
                    //? }

                    return 1;
                });
    }
}
