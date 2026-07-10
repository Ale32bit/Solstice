package me.alexdevs.solstice.modules.broadcast.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.utils.PlaceholderUtils;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.broadcast.BroadcastModule;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PlainBroadcastCommand extends ModCommand<BroadcastModule> {

    public PlainBroadcastCommand(BroadcastModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("plainbroadcast", "pbc");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("plain", 2))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            var message = StringArgumentType.getString(context, "message");
                            var serverContext = PlaceholderUtils.of(context.getSource().getServer());

                            Solstice.getInstance().broadcast(Format.parse(message, serverContext));

                            return 1;
                        }));

    }
}
