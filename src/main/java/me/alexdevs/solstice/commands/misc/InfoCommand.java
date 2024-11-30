package me.alexdevs.solstice.commands.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.commands.CommandInitializer;
import me.alexdevs.solstice.core.InfoPages;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class InfoCommand {

    private static final Predicate<ServerCommandSource> requirement = Permissions.require("solstice.command.info", true);
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // WorldEdit's /info -> /tool info
        CommandInitializer.removeCommands("info");

        // experimenting with aliases, definitely doing something wrong
        dispatcher.register(buildCommand("info"));
        dispatcher.register(buildCommand("infopage"));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildCommand(String command) {
        return literal(command)
                .requires(requirement)
                .executes(context -> {
                    var source = context.getSource();
                    var pageList = InfoPages.enumerate();
                    var sourceContext = PlaceholderContext.of(source);

                    if(pageList.isEmpty()) {
                        context.getSource().sendFeedback(() -> Format.parse(
                                Solstice.locale().commands.info.noPages,
                                sourceContext
                        ), false);
                        return 1;
                    }

                    var listText = Text.empty();
                    var comma = Format.parse(Solstice.locale().commands.info.pagesComma);
                    var list = pageList.stream().toList();
                    for(var i = 0; i < list.size(); i++) {
                        if (i > 0) {
                            listText = listText.append(comma);
                        }
                        var placeholders = Map.of(
                                "page", Text.of(list.get(i))
                        );

                        listText = listText.append(Format.parse(
                                Solstice.locale().commands.info.pagesFormat,
                                sourceContext,
                                placeholders
                        ));
                    }

                    var placeholders = Map.of(
                            "pageList", (Text) listText
                    );
                    context.getSource().sendFeedback(() -> Format.parse(
                            Solstice.locale().commands.info.pageList,
                            sourceContext,
                            placeholders
                    ), false);

                    return 1;
                })
                .then(argument("page", StringArgumentType.word())
                        .suggests((context, builder) -> CommandSource.suggestMatching(InfoPages.enumerate(), builder))
                        .executes(context -> {
                            var sourceContext = PlaceholderContext.of(context.getSource());
                            var page = InfoPages.getPage(StringArgumentType.getString(context, "page"), sourceContext);
                            context.getSource().sendFeedback(() -> page, false);
                            return 1;
                        }));

    }
}
