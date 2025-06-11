package me.alexdevs.solstice.modules.teleportPosition.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.teleportPosition.TeleportPositionModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Locale;

public class TeleportPositionCommand extends ModCommand<TeleportPositionModule> {
    public static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.teleport.invalidPosition"));

    public TeleportPositionCommand(TeleportPositionModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tppos");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(2))
                .then(Commands.argument("coordinates", Vec3Argument.vec3())
                        .executes(context -> execute(context, context.getSource().getLevel()))
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(context -> execute(context, DimensionArgument.getDimension(context, "dimension")))
                        )
                );
    }

    private static String formatFloat(double d) {
        return String.format(Locale.ROOT, "%f", d);
    }

    private int execute(CommandContext<CommandSourceStack> context, ServerLevel world) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var coords = Vec3Argument.getVec3(context, "coordinates");

        var blockPos = BlockPos.containing(coords.x(), coords.y(), coords.z());
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }

        var location = new ServerLocation(
                coords.x(), coords.y(), coords.z(),
                player.getYRot(), player.getXRot(),
                world
        );

        context.getSource().sendSuccess(() ->
                        Component.translatable("commands.teleport.success.location.single",
                                player.getDisplayName(),
                                formatFloat(coords.x), formatFloat(coords.y), formatFloat(coords.z)),
                true);

        location.teleport(player);

        return 1;
    }
}
