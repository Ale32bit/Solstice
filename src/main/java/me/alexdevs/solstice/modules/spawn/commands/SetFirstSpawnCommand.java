package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class SetFirstSpawnCommand extends ModCommand<SpawnModule> {
    public SetFirstSpawnCommand(SpawnModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("setfirstspawn");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("firstspawn.set", 2))
                .then(literal("delete")
                        .executes(this::executeDel))
                .executes(this::executeSet);
    }

    private int executeDel(CommandContext<CommandSourceStack> context) {
        var data = module.getServerData();
        data.firstSpawn = null;

        context.getSource().sendSuccess(() -> module.locale().get("firstSpawnDeleted"), true);

        return 1;
    }

    private int executeSet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var data = module.getServerData();
        data.firstSpawn = new ServerLocation(player);

        context.getSource().sendSuccess(() -> module.locale().get("firstSpawnSet"), true);

        return 1;
    }
}
