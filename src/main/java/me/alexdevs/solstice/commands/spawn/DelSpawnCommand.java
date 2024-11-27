package me.alexdevs.solstice.commands.spawn;

import me.alexdevs.solstice.Solstice;
import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class DelSpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("delspawn")
                .requires(Permissions.require("solstice.command.delspawn", 3))
                .executes(context -> {
                    var serverState = Solstice.state.getServerState();
                    serverState.spawn = null;

                    context.getSource().sendFeedback(() -> Text.literal("Server spawn deleted")
                            .formatted(Formatting.GOLD), true);

                    return 1;
                });

        dispatcher.register(rootCommand);
    }
}
