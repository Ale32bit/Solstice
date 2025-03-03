package me.alexdevs.solstice.modules.teleportPosition.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.teleportPosition.TeleportPositionModule;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

public class TeleportPositionCommand extends ModCommand<TeleportPositionModule> {
    public static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.teleport.invalidPosition"));

    public TeleportPositionCommand(TeleportPositionModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tppos");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require(2))
                .then(CommandManager.argument("coordinates", Vec3ArgumentType.vec3())
                        .executes(context -> execute(context, context.getSource().getWorld()))
                        .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                                .executes(context -> execute(context, DimensionArgumentType.getDimensionArgument(context, "dimension")))
                        )
                );
    }

    private static String formatFloat(double d) {
        return String.format(Locale.ROOT, "%f", d);
    }

    private int execute(CommandContext<ServerCommandSource> context, ServerWorld world) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var coords = Vec3ArgumentType.getVec3(context, "coordinates");

        var blockPos = BlockPos.ofFloored(coords.getX(), coords.getY(), coords.getZ());
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }

        var location = new ServerLocation(
                coords.getX(), coords.getY(), coords.getZ(),
                player.getYaw(), player.getPitch(),
                world
        );

        context.getSource().sendFeedback(() ->
                        Text.translatable("commands.teleport.success.location.single",
                                player.getDisplayName(),
                                formatFloat(coords.x), formatFloat(coords.y), formatFloat(coords.z)),
                true);

        location.teleport(player);

        return 1;
    }
}
