package me.alexdevs.solstice.modules.teleportRequest.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class TeleportAskAllHereCommand extends ModCommand<TeleportRequestModule> {
    public TeleportAskAllHereCommand(TeleportRequestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("tpaall", "tpaskall");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("here.all", 2))
                .executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        int sent = 0;

        var targets = context.getSource().getServer().getPlayerList().getPlayers();
        for (var target : targets) {
            if (ModuleProvider.IGNORE.isIgnoring(target, player) || target.equals(player)) {
                continue;
            }

            module.requestToHere(player, target, false);
            sent++;
        }

        var sourceContext = PlaceholderContext.of(player);
        player.sendSystemMessage(module.locale().get(
                "requestSentAll",
                sourceContext
        ));

        return sent;
    }
}
