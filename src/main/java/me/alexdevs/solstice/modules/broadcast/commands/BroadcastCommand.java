package me.alexdevs.solstice.modules.broadcast.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.admin.AdminModule;
import me.alexdevs.solstice.modules.broadcast.BroadcastModule;
import me.alexdevs.solstice.util.Format;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BroadcastCommand extends ModCommand<BroadcastModule> {

    public BroadcastCommand(BroadcastModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("broadcast");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            var message = StringArgumentType.getString(context, "message");
                            var serverContext = PlaceholderContext.of(context.getSource().getServer());

                            Solstice.getInstance().broadcast(Format.parse(message, serverContext));

                            return 1;
                        }));

    }
}
