package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;

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

                    item.remove(DataComponents.LORE);

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
                            var list = new ArrayList<Component>();
                            for(var line : itemLore.split("\\\\n")) {
                                list.add(Format.parse(line, playerContext));
                            }

                            item.set(DataComponents.LORE, new ItemLore(list));

                            context.getSource().sendSuccess(() -> module.locale().get("loreSet"), false);

                            return 1;
                        })
                );
    }
}
