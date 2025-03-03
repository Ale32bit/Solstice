package me.alexdevs.solstice.modules.teleportRequest.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeleportDenyCommand extends ModCommand<TeleportRequestModule> {
    public TeleportDenyCommand(TeleportRequestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpdeny", "tpno", "tprefuse");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(this::execute)
                .then(argument("player", EntityArgument.player())
                        .executes(context -> this.execute(context, EntityArgument.getPlayer(context, "player")))
                );
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var request = module.getLatestRequest(player);
        if (request == null) {
            context.getSource().sendSuccess(() -> module.locale().get("noPending"), false);
            return 0;
        }
        module.refuseRequest(player, request);

        return 1;
    }

    private int execute(CommandContext<CommandSourceStack> context, ServerPlayer source) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var request = module.getRequestFromSource(player, source);
        if (request == null) {
            context.getSource().sendSuccess(() -> module.locale().get("unavailable"), false);
            return 0;
        }
        module.refuseRequest(player, request);

        return 1;
    }
}
