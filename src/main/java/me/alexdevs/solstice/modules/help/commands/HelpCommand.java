package me.alexdevs.solstice.modules.help.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.modules.help.HelpModule;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HelpCommand extends ModCommand<HelpModule> {
    public HelpCommand(HelpModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("help");
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        Utils.removeCommands(dispatcher, "help");
        super.register(dispatcher, commandRegistry, environment);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> executeHelpList(context, 1))
                .then(argument("page", IntegerArgumentType.integer(1))
                        .executes(context -> executeHelpList(context, IntegerArgumentType.getInteger(context, "page"))))
                .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> executeHelpCommand(context, StringArgumentType.getString(context, "command"))));
    }

    private int executeHelpList(CommandContext<CommandSourceStack> context, int page) {
        var source = context.getSource();
        var commands = getCommandsPage(dispatcher.getRoot(), source, page - 1);

        var maxPages = getPageCount(dispatcher.getRoot(), source);
        var body = Component.empty();

        var header = module.locale().get("header", Map.of(
                "label", Component.literal(String.valueOf(page))
        ));

        var footer = buildFooter(page, maxPages);

        body = body.append(header).append("\n");

        int count = 0;
        for (var command : commands) {
            count++;
            var entry = module.locale().get("entry", Map.of(
                    "name", Component.literal(command.node().getName()),
                    "command", Component.literal("/" + command.usage())
            ));
            body = body.append(entry);
            body.append("\n");
        }

        body = body.append(footer);

        final var output = body;
        source.sendSuccess(() -> output, false);

        return count;
    }

    private int executeHelpCommand(CommandContext<CommandSourceStack> context, String commandName) {
        var source = context.getSource();

        var parseResults = dispatcher.parse(commandName, source);
        if (parseResults.getContext().getNodes().isEmpty()) {
            source.sendFailure(module.locale().get("notFound"));
            return 0;
        }

        var commands = getCommands(Iterables.getLast(parseResults.getContext().getNodes()).getNode(), source);
        var body = Component.empty();

        var header = module.locale().get("header", Map.of(
                "label", Component.literal(String.valueOf(commandName))
        ));

        body = body.append(header).append("\n");

        var name = parseResults.getReader().getString();

        var count = commands.size();
        for (int i = 0; i < count; i++) {
            var command = commands.get(i);
            var entry = module.locale().get("entry", Map.of(
                    "name", Component.literal(name + " " + command.node().getName()),
                    "command", Component.literal("/" + name + " " + command.usage())
            ));
            body = body.append(entry);
            if (i < count - 1) {
                body.append("\n");
            }
        }

        final var output = body;
        source.sendSuccess(() -> output, false);

        return count;
    }

    private List<CommandEntry> getCommandsPage(CommandNode<CommandSourceStack> root, CommandSourceStack source, int page) {
         var pageSize = module.getConfig().pageSize;
        return dispatcher.getSmartUsage(root, source)
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .skip((long) page * pageSize)
                .limit(pageSize)
                .map(o -> new CommandEntry(o.getKey(), o.getValue()))
                .toList();
    }

    private List<CommandEntry> getCommands(CommandNode<CommandSourceStack> root, CommandSourceStack source) {
        return dispatcher.getSmartUsage(root, source)
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(o -> new CommandEntry(o.getKey(), o.getValue()))
                .toList();
    }

    private int getPageCount(CommandNode<CommandSourceStack> root, CommandSourceStack source) {
        var pageSize = module.getConfig().pageSize;
        return (int) Math.ceil((double) dispatcher.getSmartUsage(root, source).size() / pageSize);
    }

    private Component buildFooter(int page, int maxPages) {
        Component previous;
        Component next;

        if (page > 1) {
            previous = module.locale().get("previousButton", Map.of(
                    "page", Component.literal(String.valueOf(page - 1))
            ));
        } else {
            previous = module.locale().get("previousButtonInactive");
        }

        if (page < maxPages) {
            next = module.locale().get("nextButton", Map.of(
                    "page", Component.literal(String.valueOf(page + 1))
            ));
        } else {
            next = module.locale().get("nextButtonInactive");
        }

        return module.locale().get("footer", Map.of(
                "previous", previous,
                "next", next,
                "page", Component.literal(String.valueOf(page)),
                "pages", Component.literal(String.valueOf(maxPages))
        ));
    }

    private record CommandEntry(CommandNode<CommandSourceStack> node, String usage) {
    }
}
