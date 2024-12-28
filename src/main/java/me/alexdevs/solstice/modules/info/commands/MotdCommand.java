package me.alexdevs.solstice.modules.info.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.info.InfoModule;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class MotdCommand extends ModCommand<InfoModule> {
    public MotdCommand(InfoModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("motd");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("motd", true))
                .executes(context -> {
                    var sourceContext = PlaceholderContext.of(context.getSource());

                    context.getSource().sendMessage(module.buildMotd(sourceContext));

                    return 1;
                });
    }
}
