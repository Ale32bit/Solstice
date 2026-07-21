package me.alexdevs.solstice.modules.broadcast.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.utils.PlaceholderUtils;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.broadcast.BroadcastModule;
import me.alexdevs.solstice.modules.broadcast.data.BroadcastConfig;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BroadcastCommand extends ModCommand<BroadcastModule> {

    public BroadcastCommand(BroadcastModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("broadcast", "bc");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            var config = Solstice.configManager.getData(BroadcastConfig.class);

                            var message = StringArgumentType.getString(context, "message");
                            var serverContext = PlaceholderUtils.of(context.getSource());

                            var placeholders = Map.of(
                                    "message", Format.parse(message, serverContext)
                            );

                            Solstice.getInstance().broadcast(Format.parse(config.format, placeholders));

                            return 1;
                        }));

    }
}
