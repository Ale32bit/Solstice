package me.alexdevs.solstice.modules.timeBar.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.events.TimeBarEvents;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.ComponentUtils;
import me.alexdevs.solstice.modules.timeBar.TimeBarModule;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.BossEvent;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TimeBarCommand extends ModCommand<TimeBarModule> {
    private static final ConcurrentHashMap<UUID, BarCommand> runningBars = new ConcurrentHashMap<>();

    public TimeBarCommand(TimeBarModule module) {
        super(module);

        TimeBarEvents.END.register((timeBar, server) -> {
            if (runningBars.containsKey(timeBar.getUuid())) {
                var barCommand = runningBars.get(timeBar.getUuid());
                final var command = barCommand.command();
                final var source = barCommand.source();
                runningBars.remove(timeBar.getUuid());
                Solstice.nextTick(() -> {
                    try {
                        dispatcher.execute(command, source);
                    } catch (CommandSyntaxException e) {
                        source.sendSuccess(() -> Component.literal(e.toString()).withStyle(ChatFormatting.RED), false);
                    }
                });
            }
        });
    }

    @Override
    public List<String> getNames() {
        return List.of("timebar");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(3))
                .then(literal("start")
                        .then(argument("duration", StringArgumentType.word())
                                .suggests(TimeSpan::suggest)
                                .then(argument("color", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            var colors = Arrays.stream(BossEvent.BossBarColor.values()).map(Enum::toString).toList();
                                            return SharedSuggestionProvider.suggest(colors, builder);
                                        })
                                        .then(argument("style", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    var styles = Arrays.stream(BossEvent.BossBarOverlay.values()).map(Enum::toString).toList();
                                                    return SharedSuggestionProvider.suggest(styles, builder);
                                                })
                                                .then(argument("countdown", BoolArgumentType.bool())
                                                        .then(argument("label", StringArgumentType.string())
                                                                .then(argument("command", StringArgumentType.greedyString())
                                                                        .suggests((context, builder) -> dispatcher.getRoot().listSuggestions(context, builder))
                                                                        .executes(this::execute))

                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(literal("cancel")
                        .then(argument("uuid", UuidArgument.uuid())
                                .executes(this::executeCancel)));
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var seconds = TimeSpan.getTimeSpan(context, "duration");
        var colorName = StringArgumentType.getString(context, "color");
        var styleName = StringArgumentType.getString(context, "style");
        var countdown = BoolArgumentType.getBool(context, "countdown");
        var label = StringArgumentType.getString(context, "label");
        var command = StringArgumentType.getString(context, "command");

        var color = BossEvent.BossBarColor.valueOf(colorName);
        var style = BossEvent.BossBarOverlay.valueOf(styleName);

        var bar = module.startTimeBar(label, seconds, color, style, countdown);

        runningBars.put(bar.getUuid(), new BarCommand(context.getSource(), command));

        context.getSource().sendSuccess(() -> Component
                .literal("New time bar created with UUID ")
                .append(Component.literal(bar.getUuid().toString()).setStyle(Style.EMPTY
                        .withHoverEvent(ComponentUtils.showTextHoverEvent(Component.nullToEmpty("Click to copy")))
                        .withClickEvent(ComponentUtils.clickCopyToClipboardEvent(bar.getUuid().toString())))), true);

        return 1;
    }

    private int executeCancel(CommandContext<CommandSourceStack> context) {
        var uuid = UuidArgument.getUuid(context, "uuid");

        if (!runningBars.containsKey(uuid)) {
            context.getSource().sendSuccess(() -> Component.literal("Time bar not found!").withStyle(ChatFormatting.RED), false);
            return 1;
        }

        runningBars.remove(uuid);
        module.cancelTimeBar(uuid);

        context.getSource().sendSuccess(() -> Component.literal("Time bar canceled"), true);

        return 1;
    }

    private record BarCommand(CommandSourceStack source, String command) {
    }
}
