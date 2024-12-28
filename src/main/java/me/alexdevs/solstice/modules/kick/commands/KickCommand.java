package me.alexdevs.solstice.modules.kick.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.modules.kick.KickModule;
import me.alexdevs.solstice.util.Format;
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

public class KickCommand extends ModCommand<KickModule> {
    public KickCommand(KickModule module) {
        super(module);
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

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        Utils.removeCommands(dispatcher, "kick");
        super.register(dispatcher, commandRegistry, environment);
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
}
