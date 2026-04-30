package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.utils.ItemUtils;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class ItemLoreCommand extends ModCommand<ItemModule> {
    public ItemLoreCommand(ItemModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("lore", "itemlore");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("lore", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var item = player.getMainHandItem();

                    if (item.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get("noItem"), false);
                        return 0;
                    }
                    ItemUtils.removeLore(item);
                    context.getSource().sendSuccess(() -> module.locale().get("loreCleared"), false);

                    return 1;
                })
                .then(Commands.argument("lore", StringArgumentType.greedyString())
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var item = player.getMainHandItem();
                            var itemLore = StringArgumentType.getString(context, "lore");

                            if (item.isEmpty()) {
                                context.getSource().sendSuccess(() -> module.locale().get("noItem"), false);
                                return 0;
                            }

                            var playerContext = PlaceholderContext.of(player);
                            var lines = new ArrayList<Component>();
                            for (var line : itemLore.split("\\\\n")) {
                                lines.add(Format.parse(line, playerContext));
                            }
                            ItemUtils.setLore(item, lines);
                            context.getSource().sendSuccess(() -> module.locale().get("loreSet"), false);

                            return 1;
                        })
                );
    }
}
