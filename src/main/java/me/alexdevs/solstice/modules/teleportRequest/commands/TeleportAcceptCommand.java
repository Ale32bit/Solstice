package me.alexdevs.solstice.modules.teleportRequest.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportAcceptCommand extends ModCommand<TeleportRequestModule> {
    public TeleportAcceptCommand(TeleportRequestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpaccept", "tpyes");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(this::execute)
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> this.execute(context, EntityArgumentType.getPlayer(context, "player")))
                );
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();

        var request = module.getLatestRequest(player);
        if (request == null) {
            context.getSource().sendFeedback(() -> module.locale().get("noPending"), false);
            return 0;
        }
        module.acceptRequest(player, request);

        return 1;
    }

    private int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity source) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();

        var request = module.getRequestFromSource(player, source);
        if (request == null) {
            context.getSource().sendFeedback(() -> module.locale().get("unavailable"), false);
            return 0;
        }
        module.acceptRequest(player, request);

        return 1;
    }
}
