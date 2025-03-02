package me.alexdevs.solstice.modules.miscellaneous.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.levelgen.Heightmap;
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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("top.base", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();

                    var world = player.serverLevel();
                    var top = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, player.blockPosition());
                    var pos = top.getCenter();

                    player.teleportToWithTicket(pos.x(), pos.y(), pos.z());

                    player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                    player.setOnGround(true);

                    return 1;
                });
    }
}
