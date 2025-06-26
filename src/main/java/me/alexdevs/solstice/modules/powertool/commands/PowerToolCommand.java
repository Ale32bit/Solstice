package me.alexdevs.solstice.modules.powertool.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.powertool.Action;
import me.alexdevs.solstice.modules.powertool.PowerToolModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerToolCommand extends ModCommand<PowerToolModule> {
    public PowerToolCommand(PowerToolModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("powertool", "pt");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(Action.stringValues(), builder))
                                .then(Commands.argument("command", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            var player = context.getSource().getPlayerOrException();
                                            var actionName = StringArgumentType.getString(context, "action");
                                            var command = StringArgumentType.getString(context, "command");

                                            var action = Action.valueOf(actionName.toUpperCase());

                                            var hand = player.getUsedItemHand();
                                            var item = player.getItemInHand(hand);

                                            if (item.isEmpty()) {
                                                context.getSource().sendSuccess(() -> module.locale().get("emptyHand"), false);
                                                return 0;
                                            }

                                            var data = module.getData(player.getUUID());

                                            var itemId = module.getStackId(item);

                                            var powerTool = data.powerTools.computeIfAbsent(itemId, s -> new HashMap<>());
                                            powerTool.put(action, command);

                                            var map = Map.of(
                                                    "item", Component.nullToEmpty(itemId),
                                                    "action", Component.nullToEmpty(actionName),
                                                    "command", Component.nullToEmpty(command)
                                            );

                                            context.getSource().sendSuccess(() -> module.locale().get("actionSet", map), false);

                                            return 1;
                                        })
                                )
                        ))
                .then(Commands.literal("clear")
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(Action.stringValues(), builder))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrException();
                                    var actionName = StringArgumentType.getString(context, "action");

                                    var action = Action.valueOf(actionName.toUpperCase());

                                    var hand = player.getUsedItemHand();
                                    var item = player.getItemInHand(hand);

                                    if (item.isEmpty()) {
                                        context.getSource().sendSuccess(() -> module.locale().get("emptyHand"), false);
                                        return 0;
                                    }

                                    var data = module.getData(player.getUUID());

                                    var itemId = module.getStackId(item);

                                    var map = Map.of(
                                            "item", Component.nullToEmpty(itemId),
                                            "action", Component.nullToEmpty(actionName)
                                    );

                                    if(!data.powerTools.containsKey(itemId)) {
                                        context.getSource().sendSuccess(() -> module.locale().get("noAction", map), false);
                                        return 0;
                                    }

                                    data.powerTools.get(itemId).remove(action);

                                    context.getSource().sendSuccess(() -> module.locale().get("actionCleared", map), false);

                                    return 1;
                                })
                        )
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();

                            var hand = player.getUsedItemHand();
                            var item = player.getItemInHand(hand);

                            if (item.isEmpty()) {
                                context.getSource().sendSuccess(() -> module.locale().get("emptyHand"), false);
                                return 0;
                            }

                            var data = module.getData(player.getUUID());

                            var itemId = module.getStackId(item);

                            data.powerTools.remove(itemId);

                            var map = Map.of(
                                    "item", Component.nullToEmpty(itemId)
                            );

                            context.getSource().sendSuccess(() -> module.locale().get("allCleared", map), false);

                            return 1;
                        })
                )
                .then(Commands.literal("check")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();

                            var hand = player.getUsedItemHand();
                            var item = player.getItemInHand(hand);

                            if (item.isEmpty()) {
                                context.getSource().sendSuccess(() -> module.locale().get("emptyHand"), false);
                                return 0;
                            }

                            var data = module.getData(player.getUUID());

                            var itemId = module.getStackId(item);

                            var powertool = data.powerTools.getOrDefault(itemId, Map.of());

                            var itemMap = Map.of(
                                    "item", Component.nullToEmpty(itemId)
                            );

                            var text = Component.empty();
                            text.append(module.locale().get("check", itemMap));

                            for(var action : Action.values()) {
                                text.append("\n");

                                if(powertool.containsKey(action)) {
                                    var map = Map.of(
                                            "item", Component.nullToEmpty(itemId),
                                            "action", Component.nullToEmpty(action.getSerializedName()),
                                            "command", Component.nullToEmpty(powertool.get(action))
                                    );
                                    text.append(module.locale().get("checkEntry", map));
                                } else {
                                    var map = Map.of(
                                            "item", Component.nullToEmpty(itemId),
                                            "action", Component.nullToEmpty(action.getSerializedName())
                                    );
                                    text.append(module.locale().get("checkEntryNotSet", map));
                                }
                            }

                            context.getSource().sendSuccess(() -> text, false);

                            return 1;
                        }));
    }
}
