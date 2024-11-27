package me.alexdevs.solstice.commands.warp;

import me.alexdevs.solstice.Solstice;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DeleteWarpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("delwarp")
                .requires(Permissions.require("solstice.command.delwarp", 3))
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            if (!context.getSource().isExecutedByPlayer())
                                return CommandSource.suggestMatching(new String[]{}, builder);

                            var serverState = Solstice.state.getServerState();
                            return CommandSource.suggestMatching(serverState.warps.keySet().stream(), builder);
                        })
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name"))));

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, String name) {
        var serverState = Solstice.state.getServerState();
        var warps = serverState.warps;

        if (!warps.containsKey(name)) {
            context.getSource().sendFeedback(() -> Text.literal("The warp ")
                    .append(Text.literal(name).formatted(Formatting.GOLD))
                    .append(" does not exist!")
                    .formatted(Formatting.RED), false);
            return 1;
        }

        warps.remove(name);
        Solstice.state.saveServerState();

        context.getSource().sendFeedback(() -> Text
                .literal("Warp ")
                .append(Text.literal(name).formatted(Formatting.GOLD))
                .append(" deleted!")
                .formatted(Formatting.GREEN), false);

        return 1;
    }
}
