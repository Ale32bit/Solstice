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
/*import net.minecraft.core.component.DataComponents;*/
//? }

import java.util.List;

public class ItemNameCommand extends ModCommand<ItemModule> {
    public ItemNameCommand(ItemModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("itemname");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("name", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var item = player.getMainHandItem();

                    if(item.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get("noItem"), false);
                        return 0;
                    }

                    //? if >= 1.21.1 {
                    /*item.remove(DataComponents.CUSTOM_NAME);*/
                    //? } else {
                    item.resetHoverName();
                    //? }

                    context.getSource().sendSuccess(() -> module.locale().get("nameCleared"), false);

                    return 1;
                })
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            var item = player.getMainHandItem();
                            var itemName = StringArgumentType.getString(context, "name");

                            if(item.isEmpty()) {
                                context.getSource().sendSuccess(() -> module.locale().get("noItem"), false);
                                return 0;
                            }

                            var playerContext = PlaceholderContext.of(player);
                            //? if >= 1.21.1 {
                            /*item.set(DataComponents.CUSTOM_NAME, Format.parse(itemName, playerContext));*/
                            //? } else {
                            item.setHoverName(Format.parse(itemName, playerContext));
                            //? }

                            context.getSource().sendSuccess(() -> module.locale().get("nameSet"), false);

                            return 1;
                        })
                );
    }
}
