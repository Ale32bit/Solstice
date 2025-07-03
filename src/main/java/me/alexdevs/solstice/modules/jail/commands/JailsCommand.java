package me.alexdevs.solstice.modules.jail.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.jail.JailModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JailsCommand extends ModCommand<JailModule> {
    public JailsCommand(JailModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("jails");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(2))
                .executes(this::listJails)
                .then(Commands.literal("set")
                        .requires(require("set", 3))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(this::createJail)
                        ))
                .then(Commands.literal("delete")
                        .requires(require("set", 3))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests(this::suggestJails)
                                .executes(this::deleteJail)
                        ))
                .then(Commands.literal("tp")
                        .requires(require("tp", 2))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests(this::suggestJails)
                                .executes(this::teleport)
                        ));
    }

    private CompletableFuture<Suggestions> suggestJails(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        var jails = module.getJails().keySet().stream();
        return SharedSuggestionProvider.suggest(jails, builder);
    }

    private int listJails(CommandContext<CommandSourceStack> context) {
        var jails = module.getJails().keySet().stream().toList();

        if(jails.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get("noJails"), false);
            return 0;
        }

        var comma = module.locale().get("comma");
        var list = Component.empty();

        for(var i = 0; i < jails.size(); i++) {
            if(i > 0) {
                list.append(comma);
            }

            list.append(module.locale().get("listEntry", Map.of(
                    "jail", Component.nullToEmpty(jails.get(i))
            )));
        }

        context.getSource().sendSuccess(() -> module.locale().get("jailList", Map.of(
                "list", list
        )), false);

        return 1;
    }

    private int createJail(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var jailName = StringArgumentType.getString(context, "name");

        var position = new ServerLocation(player);

        var jails = module.getJails();
        if (jails.containsKey(jailName)) {
            context.getSource().sendSuccess(() -> module.locale().get("jailAlreadyExists"), false);
            return 0;
        }

        jails.put(jailName, position);

        var map = Map.of(
                "jail", Component.nullToEmpty(jailName)
        );
        context.getSource().sendSuccess(() -> module.locale().get("created", map), true);

        return 1;
    }

    private int deleteJail(CommandContext<CommandSourceStack> context) {
        var jailName = StringArgumentType.getString(context, "name");

        var jails = module.getJails();
        if (!jails.containsKey(jailName)) {
            context.getSource().sendSuccess(() -> module.locale().get("jailNotFound"), false);
            return 0;
        }

        jails.remove(jailName);

        var map = Map.of(
                "jail", Component.nullToEmpty(jailName)
        );
        context.getSource().sendSuccess(() -> module.locale().get("deleted", map), true);

        return 1;
    }

    private int teleport(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var jailName = StringArgumentType.getString(context, "name");
        var jails = module.getJails();
        if(!jails.containsKey(jailName)) {
            context.getSource().sendSuccess(() -> module.locale().get("jailNotFound"), false);
            return 0;
        }

        var jail = jails.get(jailName);

        var map = Map.of(
                "jail", Component.nullToEmpty(jailName)
        );

        context.getSource().sendSuccess(() -> module.locale().get("teleporting", map), true);

        jail.teleport(player);

        return 1;
    }
}
