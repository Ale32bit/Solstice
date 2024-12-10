package me.alexdevs.solstice.modules.spawn.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.spawn.data.SpawnServerData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class SetSpawnCommand extends ModCommand {
    public SetSpawnCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("setspawn");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var spawnPosition = new ServerPosition(player);

                    var serverData = Solstice.serverData.getData(SpawnServerData.class);
                    serverData.spawn = spawnPosition;

                    player.getServerWorld().setSpawnPos(
                            new BlockPos(
                                    (int)spawnPosition.x,
                                    (int)spawnPosition.y,
                                    (int)spawnPosition.z
                            ),
                            spawnPosition.yaw
                    );

                    context.getSource().sendFeedback(() -> Text.literal("Server spawn set to ")
                            .append(Text.literal(String.format("%.1f %.1f %.1f", spawnPosition.x, spawnPosition.y, spawnPosition.z))
                                    .formatted(Formatting.GOLD))
                            .formatted(Formatting.GREEN), true);

                    return 1;
                });
    }
}
