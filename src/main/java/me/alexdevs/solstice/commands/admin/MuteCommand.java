package me.alexdevs.solstice.commands.admin;

import me.alexdevs.solstice.Solstice;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MuteCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var muteCommand = literal("mute")
                .requires(Permissions.require("solstice.command.mute", 2))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            var targets = GameProfileArgumentType.getProfileArgument(context, "targets");

                            var names = targets.stream().map(GameProfile::getName).toArray(String[]::new);

                            targets.forEach(profile -> {
                                var playerState = Solstice.state.getPlayerState(profile.getId());
                                playerState.muted = true;
                            });

                            Solstice.state.save();

                            context.getSource().sendFeedback(() -> Text.literal("Muted " + String.join(", ", names)), true);

                            return 1;
                        }));

        var unmuteCommand = literal("unmute")
                .requires(Permissions.require("solstice.command.mute", 2))
                .then(argument("targets", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            var targets = GameProfileArgumentType.getProfileArgument(context, "targets");

                            var names = targets.stream().map(GameProfile::getName).toArray(String[]::new);

                            targets.forEach(profile -> {
                                var playerState = Solstice.state.getPlayerState(profile.getId());
                                playerState.muted = false;
                            });

                            Solstice.state.save();

                            context.getSource().sendFeedback(() -> Text.literal("Unmuted " + String.join(", ", names)), true);

                            return 1;
                        }));

        dispatcher.register(muteCommand);
        dispatcher.register(unmuteCommand);
    }
}
