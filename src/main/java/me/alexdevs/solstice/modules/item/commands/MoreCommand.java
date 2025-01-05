package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class MoreCommand extends ModCommand<ItemModule> {
    public MoreCommand(ItemModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("more", "stack");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("more", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var item = player.getMainHandStack();

                    if(item.isEmpty()) {
                        context.getSource().sendFeedback(() -> module.locale().get("noItem"), false);
                        return 0;
                    }

                    item.setCount(item.getMaxCount());

                    context.getSource().sendFeedback(() -> module.locale().get("stackRefilled"), false);

                    return 1;
                });
    }
}
