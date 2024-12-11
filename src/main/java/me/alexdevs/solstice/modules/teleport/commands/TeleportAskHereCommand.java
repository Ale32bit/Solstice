package me.alexdevs.solstice.modules.teleport.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.modules.teleport.TeleportModule;
import me.alexdevs.solstice.modules.teleport.TeleportRequest;
import me.alexdevs.solstice.util.Components;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportAskHereCommand extends ModCommand {
    private final Locale locale = Solstice.localeManager.getLocale(TeleportModule.ID);

    public TeleportAskHereCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpahere", "tpaskhere");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .then(argument("player", StringArgumentType.word())
                        .requires(require(true))
                        .suggests((context, builder) -> {
                            var playerManager = context.getSource().getServer().getPlayerManager();
                            return CommandSource.suggestMatching(
                                    playerManager.getPlayerNames(),
                                    builder);
                        })
                        .executes(this::execute));
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = context.getSource().getPlayerOrThrow();
        var server = source.getServer();
        var targetName = StringArgumentType.getString(context, "player");
        var playerManager = server.getPlayerManager();
        var target = playerManager.getPlayer(targetName);
        var playerContext = PlaceholderContext.of(player);
        if (target == null) {
            var placeholders = Map.of(
                    "targetPlayer", Text.of(targetName)
            );
            source.sendFeedback(() -> locale.get(
                    "playerNotFound",
                    playerContext,
                    placeholders
            ), false);
            return 0;
        }

        if(ModerationModule.getPlayerData(target.getUuid()).ignoredPlayers.contains(player.getUuid())) {
            return 0;
        }

        var request = new TeleportRequest(target.getUuid(), player.getUuid());
        var targetRequests = TeleportModule.teleportRequests.get(target.getUuid());
        targetRequests.addLast(request);

        var targetContext = PlaceholderContext.of(target);
        var placeholders = Map.of(
                "requesterPlayer", player.getDisplayName(),
                "acceptButton", Components.button(
                        locale.raw("accept"),
                        locale.raw("hoverAccept"),
                        "/tpaccept " + request.requestId),
                "refuseButton", Components.button(
                        locale.raw("refuse"),
                        locale.raw("hoverRefuse"),
                        "/tpdeny " + request.requestId)
        );

        target.sendMessage(locale.get(
                "pendingTeleportHere",
                targetContext,
                placeholders
        ));

        source.sendFeedback(() -> locale.get(
                "requestSent",
                playerContext
        ), false);

        return 1;
    }
}
