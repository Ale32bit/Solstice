package me.alexdevs.solstice.modules.customname.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NicknameCommand extends ModCommand {
    public NicknameCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("nickname", "nick");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
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
    }

    private int execute(CommandContext<ServerCommandSource> context, String nickname, @Nullable ServerPlayerEntity player) throws CommandSyntaxException {
        if(player == null) {
            player = context.getSource().getPlayerOrThrow();
        }

        Solstice.modules.customName.setCustomName(player, nickname);

        var name = player.getGameProfile().getName();
        context.getSource().sendFeedback(() -> Text.literal(String.format("Changed %s's nickname", name)), true);

        return 1;
    }

    private int executeClear(CommandContext<ServerCommandSource> context, @Nullable ServerPlayerEntity player) throws CommandSyntaxException {
        if(player == null) {
            player = context.getSource().getPlayerOrThrow();
        }

        Solstice.modules.customName.clearCustomName(player);

        var name = player.getGameProfile().getName();
        context.getSource().sendFeedback(() -> Text.literal(String.format("Cleared %s's nickname", name)), true);

        return 1;
    }
}
