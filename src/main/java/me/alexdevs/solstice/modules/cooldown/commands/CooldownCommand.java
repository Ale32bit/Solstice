package me.alexdevs.solstice.modules.cooldown.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.cooldown.CooldownModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CooldownCommand extends ModCommand<CooldownModule> {
    public CooldownCommand(CooldownModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("cooldown");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(1))
                .then(Commands.literal("clear")
                        .requires(require("clear", 3))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("key", StringArgumentType.word())
                                        .suggests(this::suggestKeys)
                                        .executes(this::clearCooldown)
                                )

                        )
                )
                .then(Commands.literal("check")
                        .requires(require("check", 1))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("key", StringArgumentType.word())
                                        .suggests(this::suggestKeys)
                                        .executes(this::checkCooldown)
                                )
                        )
                );
    }

    private CompletableFuture<Suggestions> suggestKeys(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(module.getKeys(), builder);
    }

    private int clearCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");
        var key = StringArgumentType.getString(context, "key");

        if(module.isExempt(player, key)) {
            var placeholders = Map.of(
                    "player", player.getName(),
                    "key", Component.literal(key)
            );

            context.getSource().sendFailure(module.locale().get("exempt", placeholders));

            return 0;
        }

        var value = module.getCooldown(player, key);
        if(value.isEmpty()) {
            var placeholders = Map.of(
                    "player", player.getName(),
                    "key", Component.literal(key)
            );

            context.getSource().sendFailure(module.locale().get("noCooldown", placeholders));

            return 0;
        }

        module.clear(player, key);

        var placeholders = Map.of(
                "player", player.getName(),
                "key", Component.literal(key)
        );

        context.getSource().sendSuccess(() -> module.locale().get("cleared", placeholders), true);

        return 1;
    }

    private int checkCooldown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");
        var key = StringArgumentType.getString(context, "key");

        if(module.isExempt(player, key)) {
            var placeholders = Map.of(
                    "player", player.getName(),
                    "key", Component.literal(key)
            );

            context.getSource().sendFailure(module.locale().get("exempt", placeholders));

            return 0;
        }

        var value = module.getCooldown(player, key);
        if(value.isEmpty()) {
            var placeholders = Map.of(
                    "player", player.getName(),
                    "key", Component.literal(key)
            );

            context.getSource().sendFailure(module.locale().get("noCooldown", placeholders));

            return 0;
        }

        var span = TimeSpan.toShortString(value.get());

        var placeholders = Map.of(
                "player", player.getName(),
                "key", Component.literal(key),
                "timespan", Component.nullToEmpty(span)
        );

        context.getSource().sendSuccess(() -> module.locale().get("checkResult", placeholders), false);

        return 1;
    }
}
