package me.alexdevs.solstice.modules.powertool.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.powertool.Action;
import me.alexdevs.solstice.modules.powertool.PowerToolModule;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require(2))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("action", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(Action.stringValues(), builder))
                                .then(CommandManager.argument("command", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            var player = context.getSource().getPlayerOrThrow();
                                            var actionName = StringArgumentType.getString(context, "action");
                                            var command = StringArgumentType.getString(context, "command");

                                            var action = Action.valueOf(actionName.toUpperCase());

                                            var hand = player.getActiveHand();
                                            var item = player.getStackInHand(hand);

                                            if (item.isEmpty()) {
                                                context.getSource().sendFeedback(() -> module.locale().get("emptyHand"), false);
                                                return 0;
                                            }

                                            var data = module.getData(player.getUuid());

                                            var itemId = module.getStackId(item);

                                            var powerTool = data.powerTools.computeIfAbsent(itemId, s -> new HashMap<>());
                                            powerTool.put(action, command);

                                            var map = Map.of(
                                                    "item", Text.of(itemId),
                                                    "action", Text.of(actionName),
                                                    "command", Text.of(command)
                                            );

                                            context.getSource().sendFeedback(() -> module.locale().get("actionSet", map), false);

                                            return 1;
                                        })
                                )
                        ))
                .then(CommandManager.literal("clear")
                        .then(CommandManager.argument("action", StringArgumentType.word())
                                .suggests((context, builder) -> CommandSource.suggestMatching(Action.stringValues(), builder))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrThrow();
                                    var actionName = StringArgumentType.getString(context, "action");

                                    var action = Action.valueOf(actionName.toUpperCase());

                                    var hand = player.getActiveHand();
                                    var item = player.getStackInHand(hand);

                                    if (item.isEmpty()) {
                                        context.getSource().sendFeedback(() -> module.locale().get("emptyHand"), false);
                                        return 0;
                                    }

                                    var data = module.getData(player.getUuid());

                                    var itemId = module.getStackId(item);

                                    var map = Map.of(
                                            "item", Text.of(itemId),
                                            "action", Text.of(actionName)
                                    );

                                    if(!data.powerTools.containsKey(itemId)) {
                                        context.getSource().sendFeedback(() -> module.locale().get("noAction", map), false);
                                        return 0;
                                    }

                                    data.powerTools.get(itemId).remove(action);

                                    context.getSource().sendFeedback(() -> module.locale().get("actionCleared", map), false);

                                    return 1;
                                })
                        )
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();

                            var hand = player.getActiveHand();
                            var item = player.getStackInHand(hand);

                            if (item.isEmpty()) {
                                context.getSource().sendFeedback(() -> module.locale().get("emptyHand"), false);
                                return 0;
                            }

                            var data = module.getData(player.getUuid());

                            var itemId = module.getStackId(item);

                            data.powerTools.remove(itemId);

                            var map = Map.of(
                                    "item", Text.of(itemId)
                            );

                            context.getSource().sendFeedback(() -> module.locale().get("allCleared", map), false);

                            return 1;
                        })
                )
                .then(CommandManager.literal("check")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrThrow();

                            var hand = player.getActiveHand();
                            var item = player.getStackInHand(hand);

                            if (item.isEmpty()) {
                                context.getSource().sendFeedback(() -> module.locale().get("emptyHand"), false);
                                return 0;
                            }

                            var data = module.getData(player.getUuid());

                            var itemId = module.getStackId(item);

                            var powertool = data.powerTools.getOrDefault(itemId, Map.of());

                            var itemMap = Map.of(
                                    "item", Text.of(itemId)
                            );

                            var text = Text.empty();
                            text.append(module.locale().get("check", itemMap));

                            for(var action : Action.values()) {
                                text.append("\n");

                                if(powertool.containsKey(action)) {
                                    var map = Map.of(
                                            "item", Text.of(itemId),
                                            "action", Text.of(action.asString()),
                                            "command", Text.of(powertool.get(action))
                                    );
                                    text.append(module.locale().get("checkEntry", map));
                                } else {
                                    var map = Map.of(
                                            "item", Text.of(itemId),
                                            "action", Text.of(action.asString())
                                    );
                                    text.append(module.locale().get("checkEntryNotSet", map));
                                }
                            }

                            context.getSource().sendFeedback(() -> text, false);

                            return 1;
                        }));
    }
}
