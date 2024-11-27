package me.alexdevs.solstice.commands.misc;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class SuicideCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("suicide")
                .requires(Permissions.require("solstice.command.suicide", true))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    player.kill();

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
