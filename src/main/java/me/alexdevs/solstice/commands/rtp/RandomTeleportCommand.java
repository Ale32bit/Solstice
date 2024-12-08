package me.alexdevs.solstice.commands.rtp;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

import static net.minecraft.server.command.CommandManager.literal;

public class RandomTeleportCommand {
    // TODO
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("rtp")
                .requires(Permissions.require("solstice.command.rtp", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var world = player.getServerWorld();

                    var chunkManager = world.getChunkManager();

                    var worldBorder = world.getWorldBorder();
                    var size = worldBorder.getSize();
                    var centerX = worldBorder.getCenterX();
                    var centerZ = worldBorder.getCenterZ();

                    var candidateX = (int) (centerX + (Math.random() * size));
                    var candidateZ = (int) (centerZ + (Math.random() * size));

                    var blockPos = new BlockPos(candidateX, 0, candidateZ);

                    var chunk = world.getChunk(blockPos);

                    var sec = chunk.getHighestNonEmptySection();

                    var y = ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(sec));


                    player.teleport(world, candidateX, y, candidateZ, player.getYaw(), player.getPitch());



                    return 1;
                });
        //dispatcher.register(rootCommand);
    }
}
