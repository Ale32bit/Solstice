package me.alexdevs.solstice.modules.kick.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.modules.kick.KickModule;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import me.alexdevs.solstice.api.text.Format;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class KickCommand extends ModCommand<KickModule> {
    public KickCommand(KickModule module) {
        super(module);
    }

    private static int execute(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, @Nullable String reason) {
        var source = context.getSource();
        for (var target : targets) {
            var playerContext = PlaceholderContext.of(target);
            var reasonText = reason != null ? Format.parse(reason, playerContext) : Component.translatable("multiplayer.disconnect.kicked");
            target.connection.disconnect(reasonText);
            source.sendSuccess(() -> Component.translatable("commands.kick.success", target.getDisplayName(), reasonText), true);
        }

        return targets.size();
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        Utils.removeCommands(dispatcher, "kick");
        super.register(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("kick");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(argument("targets", EntityArgument.players())
                        .executes(context -> execute(context, EntityArgument.getPlayers(context, "targets"), null))
                        .then(argument("reason", StringArgumentType.greedyString())
                                .executes(context -> execute(context, EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "reason"))))
                );
    }
}
