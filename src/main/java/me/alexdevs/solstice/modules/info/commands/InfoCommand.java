package me.alexdevs.solstice.modules.info.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.Utils;
import me.alexdevs.solstice.modules.info.InfoModule;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class InfoCommand extends ModCommand {
    private InfoModule module;

    public InfoCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);

        module = Solstice.modules.info;
    }

    @Override
    public void register() {
        // WorldEdit's /info -> /tool info
        Utils.removeCommands(dispatcher, "info");
        super.register();
    }

    @Override
    public List<String> getNames() {
        return List.of("info", "pages");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {

                    var source = context.getSource();
                    var pageList = module.enumerate();
                    var sourceContext = PlaceholderContext.of(source);

                    if (pageList.isEmpty()) {
                        context.getSource().sendFeedback(() -> module.locale.get(
                                "noPages",
                                sourceContext
                        ), false);
                        return 1;
                    }

                    var listText = Text.empty();
                    var comma = module.locale.get("pagesComma");
                    var list = pageList.stream().toList();
                    for (var i = 0; i < list.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }
                        var placeholders = Map.of(
                                "page", Text.of(list.get(i))
                        );

                        listText = listText.append(module.locale.get(
                                "pagesFormat",
                                sourceContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "pageList", (Text) listText
                    );
                    context.getSource().sendFeedback(() -> module.locale.get(
                            "pageList",
                            sourceContext,
                            placeholders
                    ), false);

                    return 1;
                })
                .then(argument("page", StringArgumentType.word())
                        .suggests((context, builder) -> CommandSource.suggestMatching(module.enumerate(), builder))
                        .executes(context -> {
                            var sourceContext = PlaceholderContext.of(context.getSource());
                            var page = module.getPage(StringArgumentType.getString(context, "page"), sourceContext);
                            context.getSource().sendFeedback(() -> page, false);
                            return 1;
                        }));

    }
}
