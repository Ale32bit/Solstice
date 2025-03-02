package me.alexdevs.solstice.modules.customName.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class NicknameCommand extends ModCommand<CustomNameModule> {

    public NicknameCommand(CustomNameModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("nickname", "nick");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(literal("clear")
                        .executes(context -> executeClear(context, null))
                )
                .then(argument("nickname", StringArgumentType.string())
                        .executes(context -> execute(context, StringArgumentType.getString(context, "nickname"), null))
                )
                .then(argument("player", EntityArgument.player())
                        .requires(require("others", 2))
                        .then(literal("clear")
                                .executes(context -> executeClear(context, EntityArgument.getPlayer(context, "player")))
                        )
                        .then(argument("nickname", StringArgumentType.string())
                                .executes(context -> execute(context, StringArgumentType.getString(context, "nickname"), EntityArgument.getPlayer(context, "player")))
                        )
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, String nickname, @Nullable ServerPlayer player) throws CommandSyntaxException {
        if (player == null) {
            player = context.getSource().getPlayerOrException();
        }

        module.setCustomName(player, nickname);

        var name = player.getGameProfile().getName();
        context.getSource().sendSuccess(() -> Component.literal(String.format("Changed %s's nickname", name)), true);

        return 1;
    }

    private int executeClear(CommandContext<CommandSourceStack> context, @Nullable ServerPlayer player) throws CommandSyntaxException {
        if (player == null) {
            player = context.getSource().getPlayerOrException();
        }

        module.clearCustomName(player);

        var name = player.getGameProfile().getName();
        context.getSource().sendSuccess(() -> Component.literal(String.format("Cleared %s's nickname", name)), true);

        return 1;
    }
}
