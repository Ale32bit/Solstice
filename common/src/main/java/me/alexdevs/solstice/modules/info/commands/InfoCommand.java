package me.alexdevs.solstice.modules.info.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.module.Utils;
import me.alexdevs.solstice.modules.info.InfoModule;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InfoCommand extends ModCommand<InfoModule> {
    public InfoCommand(InfoModule module) {
        super(module);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistry, Commands.CommandSelection environment) {
        // WorldEdit's /info -> /tool info
        Utils.removeCommands(dispatcher, "info");
        super.register(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("info", "pages");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {

                    var source = context.getSource();
                    var pageList = module.enumerate();
                    var sourceContext = PlaceholderContext.of(source);

                    if (pageList.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get(
                                "noPages",
                                sourceContext
                        ), false);
                        return 1;
                    }

                    var listText = Component.empty();
                    var comma = module.locale().get("pagesComma");
                    var list = pageList.stream().toList();
                    for (var i = 0; i < list.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }
                        var placeholders = Map.of(
                                "page", Component.nullToEmpty(list.get(i))
                        );

                        listText = listText.append(module.locale().get(
                                "pagesFormat",
                                sourceContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "pageList", (Component) listText
                    );
                    context.getSource().sendSuccess(() -> module.locale().get(
                            "pageList",
                            sourceContext,
                            placeholders
                    ), false);

                    return 1;
                })
                .then(argument("page", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(module.enumerate(), builder))
                        .executes(context -> {
                            var sourceContext = PlaceholderContext.of(context.getSource());
                            var page = module.getPage(StringArgumentType.getString(context, "page"), sourceContext);
                            context.getSource().sendSuccess(() -> page, false);
                            return 1;
                        }));

    }
}
