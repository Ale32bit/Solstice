package me.alexdevs.solstice.modules.teleportRequest.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportAskHereCommand extends ModCommand<TeleportRequestModule> {
    public TeleportAskHereCommand(TeleportRequestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpahere", "tpaskhere");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("here", true))
                .then(argument("player", EntityArgumentType.player())
                        .executes(this::execute));
    }

    private int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var target = EntityArgumentType.getPlayer(context, "player");

        var ignoreModule = Solstice.modules.getModule(IgnoreModule.class);
        if (ignoreModule.getPlayerData(target.getUuid()).ignoredPlayers.contains(player.getUuid())) {
            return 0;
        }

        module.requestToHere(player, target);

        return 1;
    }
}
