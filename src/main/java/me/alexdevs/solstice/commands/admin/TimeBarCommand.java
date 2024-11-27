package me.alexdevs.solstice.commands.admin;

import me.alexdevs.solstice.api.events.BossBarEvents;
import me.alexdevs.solstice.core.BossBarManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TimeBarCommand {
    private static final ConcurrentHashMap<UUID, BarCommand> runningBars = new ConcurrentHashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("timebar")
                .requires(Permissions.require("solstice.command.timebar", 3))
                .then(literal("start")
                        .then(argument("seconds", IntegerArgumentType.integer(0))
                                .then(argument("color", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            var colors = Arrays.stream(BossBar.Color.values()).map(Enum::toString).toList();
                                            return CommandSource.suggestMatching(colors, builder);
                                        })
                                        .then(argument("style", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    var styles = Arrays.stream(BossBar.Style.values()).map(Enum::toString).toList();
                                                    return CommandSource.suggestMatching(styles, builder);
                                                })
                                                .then(argument("countdown", BoolArgumentType.bool())
                                                        .then(argument("label", StringArgumentType.string())
                                                                .then(argument("command", StringArgumentType.greedyString())
                                                                        .suggests((context, builder) -> dispatcher.getRoot().listSuggestions(context, builder))
                                                                        .executes(TimeBarCommand::execute))

                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(literal("cancel")
                        .then(argument("uuid", UuidArgumentType.uuid())
                                .executes(TimeBarCommand::executeCancel)));

        dispatcher.register(rootCommand);

        BossBarEvents.END.register((timeBar, server) -> {
            if (runningBars.containsKey(timeBar.getUuid())) {
                var barCommand = runningBars.get(timeBar.getUuid());
                try {
                    dispatcher.execute(barCommand.command(), barCommand.source);
                } catch (CommandSyntaxException e) {
                    barCommand.source.sendFeedback(() -> Text.literal(e.toString()).formatted(Formatting.RED), false);
                }
                runningBars.remove(timeBar.getUuid());
            }
        });
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        var seconds = IntegerArgumentType.getInteger(context, "seconds");
        var colorName = StringArgumentType.getString(context, "color");
        var styleName = StringArgumentType.getString(context, "style");
        var countdown = BoolArgumentType.getBool(context, "countdown");
        var label = StringArgumentType.getString(context, "label");
        var command = StringArgumentType.getString(context, "command");

        var color = BossBar.Color.valueOf(colorName);
        var style = BossBar.Style.valueOf(styleName);

        var bar = BossBarManager.getInstance().startTimeBar(label, seconds, color, style, countdown);

        runningBars.put(bar.getUuid(), new BarCommand(context.getSource(), command));


        context.getSource().sendFeedback(() -> Text
                .literal("New time bar created with UUID ")
                .append(Text.literal(bar.getUuid().toString()).setStyle(Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, bar.getUuid().toString())))), true);

        return 1;
    }

    private static int executeCancel(CommandContext<ServerCommandSource> context) {
        var uuid = UuidArgumentType.getUuid(context, "uuid");

        if (!runningBars.containsKey(uuid)) {
            context.getSource().sendFeedback(() -> Text.literal("Time bar not found!").formatted(Formatting.RED), false);
            return 1;
        }

        runningBars.remove(uuid);
        BossBarManager.getInstance().cancelTimeBar(uuid);

        context.getSource().sendFeedback(() -> Text.literal("Time bar canceled"), true);

        return 1;
    }

    private record BarCommand(ServerCommandSource source, String command) {
    }
}
