package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class DeleteSpawnCommand extends ModCommand<SpawnModule> {
    public DeleteSpawnCommand(SpawnModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("delspawn");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("set", 3))
                .executes(context -> {
                    var serverData = Solstice.serverData.getData(SpawnServerData.class);
                    serverData.spawn = null;

                    context.getSource().sendFeedback(() -> Text.literal("Server spawn deleted")
                            .formatted(Formatting.GOLD), true);

                    return 1;
                });
    }
}
