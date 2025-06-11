package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("more", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var item = player.getMainHandItem();

                    if(item.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get("noItem"), false);
                        return 0;
                    }

                    item.setCount(item.getMaxStackSize());

                    context.getSource().sendSuccess(() -> module.locale().get("stackRefilled"), false);

                    return 1;
                });
    }
}
