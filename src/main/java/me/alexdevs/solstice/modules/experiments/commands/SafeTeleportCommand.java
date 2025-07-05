package me.alexdevs.solstice.modules.experiments.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SafeTeleportCommand extends ModCommand<ExperimentsModule> {
    public SafeTeleportCommand(ExperimentsModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("safetp");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .then(Commands.argument("position", BlockPosArgument.blockPos())
                        .executes(context ->  {
                            var pos = BlockPosArgument.getBlockPos(context, "position");
                            var player = context.getSource().getPlayerOrException();

                            var loc = new ServerLocation(pos.getX(), pos.getY(), pos.getZ(), 0, 0, context.getSource().getLevel());

                            var success = loc.safeTeleport(player);
                            if (!success) {
                                context.getSource().sendFailure(Component.literal("Could not find a safe position nearby!"));
                                return 0;
                            }

                            context.getSource().sendSuccess(() -> Component.literal("Successfully teleported to a safe position!"), false);
                            return 1;
                        })
                );
    }


}
