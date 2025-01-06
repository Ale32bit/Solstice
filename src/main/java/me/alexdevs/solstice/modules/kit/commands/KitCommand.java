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
import net.minecraft.command.CommandSource;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KitCommand extends ModCommand<KitModule> {
    public KitCommand(KitModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("kit");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
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
                        )
                );
    }

    private int listKits(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var kits = getPlayerKitNames(player);

        if(kits.isEmpty()) {
            context.getSource().sendFeedback(() -> module.locale().get("listNoKits"), false);
            return 0;
        }

        var comma = module.locale().get("listComma");
        var items = Text.empty();
        for (var i = 0; i < kits.size(); i++) {
            var kit = kits.get(i);
            if (module.couldClaimKit(player, kit)) {
                items.append(module.locale().get("listAvailableKit", Map.of(
                        "kit", Text.of(kit)
                )));
            } else {
                items.append(module.locale().get("listUnavailableKit", Map.of(
                        "kit", Text.of(kit)
                )));
            }

            if (i < kits.size() - 1) {
                items.append(comma);
            }
        }

        var list = module.locale().get("listHeader", Map.of(
                "list", items
        ));

        context.getSource().sendFeedback(() -> list, false);

        return 1;
    }

    private int claimKit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();
        var name = StringArgumentType.getString(context, "name");

        if (!module.hasKitPermission(player, name)) {
            source.sendFeedback(() -> module.locale().get("noPermission", Map.of("kit", Text.of(name))), false);
            return 0;
        }

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            source.sendFeedback(() -> module.locale().get("notFound", Map.of("kit", Text.of(name))), false);
            return 0;
        }

        var kit = kits.get(name);

        var playerData = Solstice.playerData.get(player).getData(KitPlayerData.class);
        if (kit.oneTime && playerData.claimedKits.containsKey(name)) {
            source.sendFeedback(() -> module.locale().get("alreadyClaimed", Map.of("kit", Text.of(name))), false);
            return 0;
        }

        if (kit.cooldownSeconds > 0) {
            if (playerData.claimedKits.containsKey(name)) {
                var startDate = playerData.claimedKits.get(name);
                var nowDate = new Date();

                var delta = (nowDate.getTime() - startDate.getTime()) / 1000;
                if (delta < kit.cooldownSeconds) {
                    var remaining = kit.cooldownSeconds - delta;

                    var timespan = TimeSpan.serialize((int) remaining);
                    source.sendFeedback(() -> module.locale().get("onCooldown", Map.of(
                            "kit", Text.of(name),
                            "timespan", Text.of(timespan)
                    )), false);
                    return 0;
                }
            }
        }

        module.claimKit(player, name);

        source.sendFeedback(() -> module.locale().get("claimed", Map.of("kit", Text.of(name))), false);

        return 1;
    }

    private int createKit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();
        var name = StringArgumentType.getString(context, "name");

        if (module.getKits().containsKey(name)) {
            source.sendFeedback(() -> module.locale().get("alreadyExists"), false);
            return 0;
        }

        var kitInventory = new KitInventory();
        var container = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public void onClose() {
                if (module.createKit(name, Utils.getItemStacks(kitInventory))) {
                    source.sendFeedback(() -> module.locale().get("created", Map.of("kit", Text.of(name))), true);
                } else {
                    source.sendFeedback(() -> module.locale().get("alreadyExists"), false);
                }
            }
        };

        Utils.redirect(container, kitInventory);
        container.setTitle(module.locale().get("newKitTitle", Map.of("kit", Text.of(name))));
        container.open();

        return 1;
    }

    private int deleteKit(CommandContext<ServerCommandSource> context) {
        var name = StringArgumentType.getString(context, "name");
        if (module.getKits().remove(name) != null) {
            context.getSource().sendFeedback(() -> module.locale().get("deleted", Map.of("kit", Text.of(name))), true);
        } else {
            context.getSource().sendFeedback(() -> module.locale().get("notFound", Map.of("kit", Text.of(name))), false);
        }

        return 1;
    }

    private int editKit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();
        var name = StringArgumentType.getString(context, "name");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendFeedback(() -> module.locale().get("notFound", Map.of("kit", Text.of(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        var kitInventory = Utils.createInventory(kit.getItemStacks());

        var container = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public void onClose() {
                var items = Utils.getItemStacks(kitInventory);
                kit.itemStacks = items.stream().map(Utils::serializeItemStack).toList();
                source.sendFeedback(() -> module.locale().get("edited", Map.of("kit", Text.of(name))), true);
            }
        };

        Utils.redirect(container, kitInventory);
        container.setTitle(module.locale().get("editKitTitle", Map.of("kit", Text.of(name))));
        container.open();

        return 1;
    }

    private int setFirstJoin(CommandContext<ServerCommandSource> context) {
        var name = StringArgumentType.getString(context, "name");
        var enable = BoolArgumentType.getBool(context, "enable");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendFeedback(() -> module.locale().get("notFound", Map.of("kit", Text.of(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        kit.firstJoin = enable;

        context.getSource().sendFeedback(() -> module.locale().get("setFirstJoin", Map.of(
                "kit", Text.of(name),
                "value", Text.of(String.valueOf(enable))
        )), true);

        return 1;
    }

    private int setCooldown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var name = StringArgumentType.getString(context, "name");
        var timespan = TimeSpan.getTimeSpan(context, "timespan");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendFeedback(() -> module.locale().get("notFound", Map.of("kit", Text.of(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        kit.cooldownSeconds = timespan;

        context.getSource().sendFeedback(() -> module.locale().get("setFirstJoin", Map.of(
                "kit", Text.of(name),
                "value", Text.of(TimeSpan.serialize(timespan))
        )), true);

        return 1;
    }

    private int setOneTime(CommandContext<ServerCommandSource> context) {
        var name = StringArgumentType.getString(context, "name");
        var enable = BoolArgumentType.getBool(context, "enable");

        var kits = module.getKits();
        if (!kits.containsKey(name)) {
            context.getSource().sendFeedback(() -> module.locale().get("notFound", Map.of("kit", Text.of(name))), false);
            return 0;
        }

        var kit = kits.get(name);
        kit.oneTime = enable;

        context.getSource().sendFeedback(() -> module.locale().get("setFirstJoin", Map.of(
                "kit", Text.of(name),
                "value", Text.of(String.valueOf(enable))
        )), true);

        return 1;
    }

    private CompletableFuture<Suggestions> suggestAllKits(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        var kits = module.getKits().keySet();
        return CommandSource.suggestMatching(kits, builder);
    }

    private CompletableFuture<Suggestions> suggestKitList(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        return CommandSource.suggestMatching(getPlayerKitNames(player), builder);
    }

    private List<String> getPlayerKitNames(ServerPlayerEntity player) {
        return module.getKits().keySet().stream().filter(kit -> module.hasKitPermission(player, kit)).toList();
    }
}
