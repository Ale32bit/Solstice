package me.alexdevs.solstice.modules.afk.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.afk.AfkModuleOld;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class AfkCommand extends ModCommand<AfkModule> {
    public AfkCommand(AfkModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("afk");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    module.setPlayerAfk(player, true);

                    return 1;
                });
    }
}
