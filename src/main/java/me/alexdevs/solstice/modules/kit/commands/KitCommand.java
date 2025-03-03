package me.alexdevs.solstice.modules.kit.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.kit.KitInventory;
import me.alexdevs.solstice.modules.kit.KitModule;
import me.alexdevs.solstice.modules.kit.Utils;
import me.alexdevs.solstice.modules.kit.data.KitPlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class KitCommand extends ModCommand<KitModule> {
    public KitCommand(KitModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("kit");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(literal("list")
                        .executes(this::listKits))
                .then(literal("claim")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(this::suggestKitList)
                                .executes(this::claimKit))
                )
                .then(literal("create")
                        .requires(require("set", 3))
                        .then(argument("name", StringArgumentType.word())
                                .executes(this::createKit))
                )
                .then(literal("delete")
                        .requires(require("set", 3))
                        .then(argument("name", StringArgumentType.word())
                                .suggests(this::suggestAllKits)
                                .executes(this::deleteKit))
                )
                .then(literal("edit")
                        .requires(require("set", 3))
                        .then(argument("name", StringArgumentType.word())
                                .suggests(this::suggestKitList)
                                .executes(this::editKit))
                )
                .then(literal("set")
                        .requires(require("set", 3))
                        .then(argument("name", StringArgumentType.word())
                                .suggests(this::suggestAllKits)
                                .then(literal("first-join")
                                        .then(argument("enable", BoolArgumentType.bool())
                                                .executes(this::setFirstJoin)
                                        ))
                                .then(literal("cooldown")
                                        .then(argument("timespan", StringArgumentType.word())
                                                .suggests(TimeSpan::suggest)
                                                .executes(this::setCooldown)
                                        ))
                                .then(literal("one-time")
                                        .then(argument("enable", BoolArgumentType.bool())
                                                .executes(this::setOneTime)
                                        ))
                                .then(literal("icon")
                                        .executes(this::setIcon))
                        )
                );
    }

    private int listKits(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var kits = module.getPlayerKitNames(player);

        if(kits.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get("listNoKits"), false);
            return 0;
        }

        var comma = module.locale().get("listComma");
        var items = Component.empty();
        for (var i = 0; i < kits.size(); i++) {
            var kit = kits.get(i);
            if (module.couldClaimKit(player, kit)) {
                items.append(module.locale().get("listAvailableKit", Map.of(
                        "kit", Component.nullToEmpty(kit)
                )));
            } else {
                items.append(module.locale().get("listUnavailableKit", Map.of(
                        "kit", Component.nullToEmpty(kit)
                )));
            }

            if (i < kits.size() - 1) {
                items.append(comma);
            }
        }

        var list = module.locale().get("listHeader", Map.of(
                "list", items
        ));

        context.getSource().sendSuccess(() -> list, false);

        return 1;
    }

    private int claimKit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var name = StringArgumentType.getString(context, "name");

        if (!module.hasKitPermission(player, name)) {
            source.sendSuccess(() -> module.locale().get("noPermission", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            source.sendSuccess(() -> module.locale().get("notFound", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        var kit = kits.get(name);

        var playerData = Solstice.playerData.get(player).getData(KitPlayerData.class);
        if (kit.oneTime && playerData.claimedKits.containsKey(name)) {
            source.sendSuccess(() -> module.locale().get("alreadyClaimed", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        if (kit.cooldownSeconds > 0) {
            if (playerData.claimedKits.containsKey(name)) {
                var startDate = playerData.claimedKits.get(name);
                var nowDate = new Date();

                var delta = (nowDate.getTime() - startDate.getTime()) / 1000;
                if (delta < kit.cooldownSeconds) {
                    var remaining = kit.cooldownSeconds - delta;

                    var timespan = TimeSpan.toShortString((int) remaining);
                    source.sendSuccess(() -> module.locale().get("onCooldown", Map.of(
                            "kit", Component.nullToEmpty(name),
                            "timespan", Component.nullToEmpty(timespan)
                    )), false);
                    return 0;
                }
            }
        }

        module.claimKit(player, name);

        source.sendSuccess(() -> module.locale().get("claimed", Map.of("kit", Component.nullToEmpty(name))), false);

        return 1;
    }

    private int createKit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var name = StringArgumentType.getString(context, "name");

        if (module.getKits().containsKey(name)) {
            source.sendSuccess(() -> module.locale().get("alreadyExists"), false);
            return 0;
        }

        var kitInventory = new KitInventory();
        var container = new SimpleGui(MenuType.GENERIC_9x3, player, false) {
            @Override
            public void onClose() {
                if (module.createKit(name, Utils.getItemStacks(kitInventory))) {
                    source.sendSuccess(() -> module.locale().get("created", Map.of("kit", Component.nullToEmpty(name))), true);
                } else {
                    source.sendSuccess(() -> module.locale().get("alreadyExists"), false);
                }
            }
        };

        Utils.redirect(container, kitInventory);
        container.setTitle(module.locale().get("newKitTitle", Map.of("kit", Component.nullToEmpty(name))));
        container.open();

        return 1;
    }

    private int deleteKit(CommandContext<CommandSourceStack> context) {
        var name = StringArgumentType.getString(context, "name");
        if (module.getKits().remove(name) != null) {
            context.getSource().sendSuccess(() -> module.locale().get("deleted", Map.of("kit", Component.nullToEmpty(name))), true);
        } else {
            context.getSource().sendSuccess(() -> module.locale().get("notFound", Map.of("kit", Component.nullToEmpty(name))), false);
        }

        return 1;
    }

    private int editKit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();
        var name = StringArgumentType.getString(context, "name");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendSuccess(() -> module.locale().get("notFound", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        var kitInventory = Utils.createInventory(kit.getItemStacks());

        var container = new SimpleGui(MenuType.GENERIC_9x3, player, false) {
            @Override
            public void onClose() {
                var items = Utils.getItemStacks(kitInventory);
                kit.itemStacks = items.stream().map(Utils::serializeItemStack).toList();
                source.sendSuccess(() -> module.locale().get("edited", Map.of("kit", Component.nullToEmpty(name))), true);
            }
        };

        Utils.redirect(container, kitInventory);
        container.setTitle(module.locale().get("editKitTitle", Map.of("kit", Component.nullToEmpty(name))));
        container.open();

        return 1;
    }

    private int setFirstJoin(CommandContext<CommandSourceStack> context) {
        var name = StringArgumentType.getString(context, "name");
        var enable = BoolArgumentType.getBool(context, "enable");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendSuccess(() -> module.locale().get("notFound", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        kit.firstJoin = enable;

        context.getSource().sendSuccess(() -> module.locale().get("setFirstJoin", Map.of(
                "kit", Component.nullToEmpty(name),
                "value", Component.nullToEmpty(String.valueOf(enable))
        )), true);

        return 1;
    }

    private int setCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var name = StringArgumentType.getString(context, "name");
        var timespan = TimeSpan.getTimeSpan(context, "timespan");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendSuccess(() -> module.locale().get("notFound", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        kit.cooldownSeconds = timespan;

        context.getSource().sendSuccess(() -> module.locale().get("setCooldown", Map.of(
                "kit", Component.nullToEmpty(name),
                "value", Component.nullToEmpty(TimeSpan.toShortString(timespan))
        )), true);

        return 1;
    }

    private int setOneTime(CommandContext<CommandSourceStack> context) {
        var name = StringArgumentType.getString(context, "name");
        var enable = BoolArgumentType.getBool(context, "enable");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendSuccess(() -> module.locale().get("notFound", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        kit.oneTime = enable;

        context.getSource().sendSuccess(() -> module.locale().get("setOneTime", Map.of(
                "kit", Component.nullToEmpty(name),
                "value", Component.nullToEmpty(String.valueOf(enable))
        )), true);

        return 1;
    }

    private int setIcon(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var name = StringArgumentType.getString(context, "name");
        var player = context.getSource().getPlayerOrException();

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendSuccess(() -> module.locale().get("notFound", Map.of("kit", Component.nullToEmpty(name))), false);
            return 0;
        }

        var hand = player.getUsedItemHand();
        var stack = player.getItemInHand(hand).copy();
        if(stack.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get("noStackInHand"), false);
            return 0;
        }

        var kit = kits.get(name);
        kit.icon = Utils.serializeItemStack(stack);

        context.getSource().sendSuccess(() -> module.locale().get("setIcon", Map.of(
                "kit", Component.nullToEmpty(name)
        )), true);

        return 1;
    }

    private CompletableFuture<Suggestions> suggestAllKits(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        var kits = module.getKits().keySet();
        return SharedSuggestionProvider.suggest(kits, builder);
    }

    private CompletableFuture<Suggestions> suggestKitList(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        return SharedSuggestionProvider.suggest(module.getPlayerKitNames(player), builder);
    }
}
