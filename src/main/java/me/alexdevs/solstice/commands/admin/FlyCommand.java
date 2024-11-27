package me.alexdevs.solstice.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FlyCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("fly")
                .requires(Permissions.require("solstice.command.fly", 3))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    context.getSource().sendFeedback(() -> toggleFlight(player), true);

                    return 1;
                })
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            var playerManager = context.getSource().getServer().getPlayerManager();
                            return CommandSource.suggestMatching(
                                    playerManager.getPlayerNames(),
                                    builder);
                        })
                        .executes(context -> {
                            var playerName = StringArgumentType.getString(context, "player");
                            var player = context.getSource().getServer().getPlayerManager().getPlayer(playerName);
                            if (player == null) {
                                context.getSource().sendFeedback(() -> Text.literal("Player not found").setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                                return 1;
                            }

                            context.getSource().sendFeedback(() -> toggleFlight(player), true);

                            return 1;
                        }));

        dispatcher.register(rootCommand);
    }

    private static Text toggleFlight(ServerPlayerEntity player) {
        var abilities = player.getAbilities();

        abilities.allowFlying = !abilities.allowFlying;
        player.sendAbilitiesUpdate();

        return Text.literal(
                        abilities.allowFlying ?
                                "Flight enabled" :
                                "Flight disabled"
                )
                .append(" for ")
                .append(player.getDisplayName())
                .setStyle(Style.EMPTY.withColor(
                        abilities.allowFlying ?
                                Formatting.GREEN :
                                Formatting.RED
                ));
    }
}
