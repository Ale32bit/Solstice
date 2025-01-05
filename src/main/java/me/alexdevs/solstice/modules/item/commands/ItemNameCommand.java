package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("name", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    var item = player.getMainHandStack();

                    if(item.isEmpty()) {
                        context.getSource().sendFeedback(() -> module.locale().get("noItem"), false);
                        return 0;
                    }

                    item.removeCustomName();

                    context.getSource().sendFeedback(() -> module.locale().get("nameCleared"), false);

                    return 1;
                })
                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();
                            var item = player.getMainHandStack();
                            var itemName = StringArgumentType.getString(context, "name");

                            if(item.isEmpty()) {
                                context.getSource().sendFeedback(() -> module.locale().get("noItem"), false);
                                return 0;
                            }

                            var playerContext = PlaceholderContext.of(player);
                            item.setCustomName(Format.parse(itemName, playerContext));

                            context.getSource().sendFeedback(() -> module.locale().get("nameSet"), false);

                            return 1;
                        })
                );
    }
}
