package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
//? if >= 1.21.1 {
/*import net.minecraft.core.component.DataComponents;
*///? }
//? if < 1.21.1 {
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
//? }
import net.minecraft.network.chat.Component;
//? if >= 1.21.1 {
/*import net.minecraft.world.item.component.ItemLore;
*///? }

//? if >= 1.21.1 {
/*import java.util.ArrayList;
*///? }
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

                    //? if >= 1.21.1 {
                    /*item.remove(DataComponents.LORE);
                    *///? } else {
                    CompoundTag nbtCompound = item.getTagElement("display");
                    if (nbtCompound != null) {
                        nbtCompound.remove("Lore");
                        if (nbtCompound.isEmpty()) {
                            item.removeTagKey("display");
                        }
                    }
                    //? }

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
                            //? if >= 1.21.1 {
                            /*var list = new ArrayList<Component>();
                            for (var line : itemLore.split("\\\\n")) {
                                list.add(Format.parse(line, playerContext));
                            }
                            item.set(DataComponents.LORE, new ItemLore(list));
                            *///? } else {
                            var list = new ListTag();
                            for (var line : itemLore.split("\\\\n")) {
                                var text = Format.parse(line, playerContext);
                                list.add(StringTag.valueOf(Component.Serializer.toJson(text)));
                            }
                            var displayNbt = item.getOrCreateTagElement("display");
                            displayNbt.put("Lore", list);
                            //? }

                            context.getSource().sendSuccess(() -> module.locale().get("loreSet"), false);

                            return 1;
                        })
                );
    }
}
