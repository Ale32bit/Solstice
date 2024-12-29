package me.alexdevs.solstice.modules.kit.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.kit.KitModule;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
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
                .executes(this::listKits)
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
                                .then(literal("first-join"))
                                .then(literal("cooldown"))
                                .then(literal("one-time"))
                        )
                )
                .then(argument("name", StringArgumentType.word())
                        .suggests(this::suggestKitList)
                        .executes(this::claimKit));
    }

    private int listKits(CommandContext<ServerCommandSource> context) {
        return 1;
    }

    private int editKit(CommandContext<ServerCommandSource> context) {
        return 1;
    }

    private int claimKit(CommandContext<ServerCommandSource> context) {
        return 1;
    }

    private int deleteKit(CommandContext<ServerCommandSource> context) {
        return 1;
    }

    private int createKit(CommandContext<ServerCommandSource> context) {
        return 1;
    }

    private CompletableFuture<Suggestions> suggestAllKits(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        var kits = module.getKits().keySet();
        return CommandSource.suggestMatching(kits, builder);
    }

    private CompletableFuture<Suggestions> suggestKitList(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        var kits = module.getKits().keySet();
        return CommandSource.suggestMatching(kits, builder);
    }
}
