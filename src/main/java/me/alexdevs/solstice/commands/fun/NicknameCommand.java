package me.alexdevs.solstice.commands.fun;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.CustomNameFormat;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NicknameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("nick")
                .requires(Permissions.require("solstice.command.nick", 2))
                .then(literal("clear")
                        .executes(context -> executeClear(context, null))
                )
                .then(argument("nickname", StringArgumentType.string())
                        .executes(context -> execute(context, StringArgumentType.getString(context, "nickname"), null))
                )
                .then(argument("player", EntityArgumentType.player())
                        .then(literal("clear")
                                .executes(context -> executeClear(context, EntityArgumentType.getPlayer(context, "player")))
                        )
                        .then(argument("nickname", StringArgumentType.string())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "nickname"), EntityArgumentType.getPlayer(context, "player")))
                        )
                );

        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, String nickname, @Nullable ServerPlayerEntity player) throws CommandSyntaxException {
        if(player == null) {
            player = context.getSource().getPlayerOrThrow();
        }

        var playerState = Solstice.state.getPlayerState(player.getUuid());
        playerState.nickname = nickname;
        CustomNameFormat.refreshName(player);

        var name = player.getGameProfile().getName();
        context.getSource().sendFeedback(() -> Text.literal(String.format("Changed %s's nickname", name)), true);

        return 1;
    }

    private static int executeClear(CommandContext<ServerCommandSource> context, @Nullable ServerPlayerEntity player) throws CommandSyntaxException {
        if(player == null) {
            player = context.getSource().getPlayerOrThrow();
        }

        var playerState = Solstice.state.getPlayerState(player.getUuid());
        playerState.nickname = null;
        CustomNameFormat.refreshName(player);

        var name = player.getGameProfile().getName();
        context.getSource().sendFeedback(() -> Text.literal(String.format("Cleared %s's nickname", name)), true);

        return 1;
    }
}
