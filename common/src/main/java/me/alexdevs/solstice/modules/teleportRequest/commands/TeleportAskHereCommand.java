package me.alexdevs.solstice.modules.teleportRequest.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeleportAskHereCommand extends ModCommand<TeleportRequestModule> {
    public TeleportAskHereCommand(TeleportRequestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpahere", "tpaskhere");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("here", true))
                .then(argument("player", EntityArgument.player())
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var target = EntityArgument.getPlayer(context, "player");

        var ignoreModule = Solstice.modules.getModule(IgnoreModule.class);
        if (ignoreModule.getPlayerData(target.getUUID()).ignoredPlayers.contains(player.getUUID())) {
            return 0;
        }

        module.requestToHere(player, target);

        return 1;
    }
}
