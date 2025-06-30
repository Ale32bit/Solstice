package me.alexdevs.solstice.modules.teleportRequest.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TeleportAskCommand extends ModCommand<TeleportRequestModule> {
    public TeleportAskCommand(TeleportRequestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpa", "tpask");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("ask", true))
                .then(argument("player", EntityArgument.player())
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var target = EntityArgument.getPlayer(context, "player");

        var ignoreModule = ModuleProvider.IGNORE;
        if (ignoreModule.isEnabled() && ignoreModule.getPlayerData(target.getUUID()).ignoredPlayers.contains(player.getUUID())) {
            return 0;
        }

        module.requestTo(player, target);

        return 1;
    }
}
