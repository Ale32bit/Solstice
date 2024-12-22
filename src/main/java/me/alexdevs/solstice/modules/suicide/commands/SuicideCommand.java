package me.alexdevs.solstice.modules.suicide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.suicide.SuicideModule;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class SuicideCommand extends ModCommand<SuicideModule> {
    public SuicideCommand(SuicideModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("suicide");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    player.kill();

                    return 1;
                });
    }
}
