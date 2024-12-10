package me.alexdevs.solstice.modules.moderation.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.Utils;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KickCommand extends ModCommand {
    public KickCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public void register() {
        Utils.removeCommands(dispatcher, "kick");
        super.register();
    }

    @Override
    public List<String> getNames() {
        return List.of("kick");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(argument("targets", EntityArgumentType.players())
                        .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "targets"), null))
                        .then(argument("reason", StringArgumentType.greedyString())
                                .executes(context -> execute(context, EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context, "reason"))))
                );
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> targets, @Nullable String reason) {
        var source = context.getSource();
        for (var target : targets) {
            var playerContext = PlaceholderContext.of(target);
            var reasonText = reason != null ? Format.parse(reason, playerContext) : Text.translatable("multiplayer.disconnect.kicked");
            target.networkHandler.disconnect(reasonText);
            source.sendFeedback(() -> Text.translatable("commands.kick.success", target.getDisplayName(), reasonText), true);
        }

        return targets.size();
    }
}
