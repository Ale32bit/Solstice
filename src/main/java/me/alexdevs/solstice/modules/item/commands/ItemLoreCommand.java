package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("lore", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var item = player.getMainHandStack();

                    if (item.isEmpty()) {
                        context.getSource().sendFeedback(() -> module.locale().get("noItem"), false);
                        return 0;
                    }

                    item.remove(DataComponentTypes.LORE);

                    context.getSource().sendFeedback(() -> module.locale().get("loreCleared"), false);

                    return 1;
                })
                .then(CommandManager.argument("lore", StringArgumentType.greedyString())
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();
                            var item = player.getMainHandStack();
                            var itemLore = StringArgumentType.getString(context, "lore");

                            if (item.isEmpty()) {
                                context.getSource().sendFeedback(() -> module.locale().get("noItem"), false);
                                return 0;
                            }


                            var playerContext = PlaceholderContext.of(player);
                            var list = new ArrayList<Text>();
                            for(var line : itemLore.split("\\\\n")) {
                                list.add(Format.parse(line, playerContext));
                            }

                            item.set(DataComponentTypes.LORE, new LoreComponent(list));

                            context.getSource().sendFeedback(() -> module.locale().get("loreSet"), false);

                            return 1;
                        })
                );
    }
}
