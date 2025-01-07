package me.alexdevs.solstice.modules.core.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.core.CoreModule;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerStatCommand extends ModCommand<CoreModule> {
    public ServerStatCommand(CoreModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("serverstat", "tps");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("serverstat", 3))
                .executes(context -> {
                    var locale = module.locale();
                    var placeholderContext = PlaceholderContext.of(context.getSource());

                    var messages = new ArrayList<Text>();

                    messages.add(locale.get("stat.tps", placeholderContext));

                    var uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
                    var uptimeFormatted = TimeSpan.serialize((int)uptime.getSeconds());
                    messages.add(locale.get("stat.uptime", placeholderContext, Map.of(
                            "uptime", Text.of(uptimeFormatted)
                    )));

                    var maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
                    var allocatedMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                    var freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;

                    messages.add(locale.get("stat.maxMemory", placeholderContext, Map.of(
                            "memory", Text.of(String.valueOf(maxMemory)),
                            "hover", locale.get("stat.maxMemory.hover")
                    )));

                    messages.add(locale.get("stat.dedicatedMemory", placeholderContext, Map.of(
                            "memory", Text.of(String.valueOf(allocatedMemory)),
                            "hover", locale.get("stat.dedicatedMemory.hover")

                    )));

                    messages.add(locale.get("stat.freeMemory", placeholderContext, Map.of(
                            "memory", Text.of(String.valueOf(freeMemory)),
                            "hover", locale.get("stat.freeMemory.hover")
                    )));

                    var text = Text.empty();
                    text.append(locale.get("stat.title"));

                    for(var message : messages) {
                        text.append(Text.of("\n"));
                        text.append(message);
                    }

                    context.getSource().sendFeedback(() -> text, false);

                    return 1;
                });
    }
}
