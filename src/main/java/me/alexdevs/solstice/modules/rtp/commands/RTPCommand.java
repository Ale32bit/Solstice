package me.alexdevs.solstice.modules.rtp.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.rtp.RTPModule;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static net.minecraft.server.command.CommandManager.literal;

public class RTPCommand extends ModCommand<RTPModule> {
    public RTPCommand(RTPModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("rtp");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var config = module.getConfig();
                    if (config.enableCooldown) {
                        if (!Solstice.cooldown.trigger(player, module.getPermissionNode(), config.cooldown)) {
                            context.getSource().sendFeedback(() -> module.locale().get("~cooldown"), false);
                            return 0;
                        }
                    }

                    context.getSource().sendFeedback(() -> module.locale().get("searching"), false);

                    return 1;
                });
    }
}
