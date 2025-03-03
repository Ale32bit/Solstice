package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.literal;

public class SetSpawnCommand extends ModCommand<SpawnModule> {
    public SetSpawnCommand(SpawnModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("setspawn");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("set", 3))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var spawnPosition = new ServerLocation(player);
                    var world = player.serverLevel();


                    world.setDefaultSpawnPos(
                            player.blockPosition(),
                            spawnPosition.getYaw()
                    );

                    context.getSource().sendSuccess(() -> module.locale().get("worldSpawnSet", Map.of(
                            "world", Component.nullToEmpty(world.dimension().location().toString()),
                            "coordinates", Component.nullToEmpty(String.format("%.1f %.1f %.1f", spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()))
                    )), true);

                    return 1;
                });
    }
}
