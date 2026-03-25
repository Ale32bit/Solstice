package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

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
                    var location = new ServerLocation(player);
                    var world = PlayerUtils.getLevel(player);

                    // world spawn point is ignored on non-overworld levels
                    if(world.dimension() == Level.OVERWORLD) {
                        //? >= 1.21.11
                        //world.setRespawnData(new LevelData.RespawnData(new GlobalPos(location.getWorldKey(),new BlockPos(location.getBlockPos().getX(), location.getBlockPos().getY(), location.getBlockPos().getZ())), location.getYaw(), location.getPitch()));
                        //? < 1.21.11
                        world.setDefaultSpawnPos(location.getBlockPos(), location.getYaw());
                    } else {
                        module.getServerData().spawnPoints.put(location.getWorld(), location);
                    }

                    context.getSource().sendSuccess(() -> module.locale().get("worldSpawnSet", Map.of(
                            "world", Component.nullToEmpty(world.dimension().location().toString()),
                            "coordinates", Component.nullToEmpty(String.format("%.1f %.1f %.1f", location.getX(), location.getY(), location.getZ()))
                    )), true);

                    return 1;
                });
    }
}
