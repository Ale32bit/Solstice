package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class SetSpawnCommand extends ModCommand<SpawnModule> {
    public SetSpawnCommand(SpawnModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("setspawn");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("set", 3))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var spawnPosition = new ServerPosition(player);
                    var world = player.getServerWorld();


                    world.setSpawnPos(
                            player.getBlockPos(),
                            spawnPosition.yaw
                    );

                    context.getSource().sendFeedback(() -> module.locale().get("worldSpawnSet", Map.of(
                            "world", Text.of(world.getRegistryKey().getValue().toString()),
                            "coordinates", Text.of(String.format("%.1f %.1f %.1f", spawnPosition.x, spawnPosition.y, spawnPosition.z))
                    )), true);

                    return 1;
                });
    }
}
