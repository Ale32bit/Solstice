package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.Heightmap;

import java.util.List;

public class TopCommand extends ModCommand<MiscellaneousModule> {
    public TopCommand(MiscellaneousModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("top");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("top.base", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    var world = player.getServerWorld();
                    var top = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, player.getBlockPos());
                    var pos = top.toCenterPos();

                    player.teleport(pos.getX(), pos.getY(), pos.getZ(), false);

                    player.setVelocity(player.getVelocity().multiply(1.0, 0.0, 1.0));
                    player.setOnGround(true);

                    return 1;
                });
    }
}
