package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
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

                    NbtCompound nbtCompound = item.getSubNbt("display");
                    if (nbtCompound != null) {
                        nbtCompound.remove("Lore");
                        if (nbtCompound.isEmpty()) {
                            item.removeSubNbt("display");
                        }
                    }

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


                            var displayNbt = item.getOrCreateSubNbt("display");
                            var list = new NbtList();

                            var playerContext = PlaceholderContext.of(player);
                            for(var line : itemLore.split("\\\\n")) {
                                var text = Format.parse(line, playerContext);
                                list.add(NbtString.of(Text.Serializer.toJson(text)));
                            }

                            displayNbt.put("Lore", list);

                            context.getSource().sendFeedback(() -> module.locale().get("loreSet"), false);

                            return 1;
                        })
                );
    }
}
