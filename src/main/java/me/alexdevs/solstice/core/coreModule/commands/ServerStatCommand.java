package me.alexdevs.solstice.core.coreModule.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.utils.PlaceholderUtils;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("serverstat", 3))
                .executes(context -> {
                    var locale = module.locale();
                    var placeholderContext = PlaceholderUtils.of(context.getSource());

                    var messages = new ArrayList<Component>();

                    messages.add(locale.get("stat.tps", placeholderContext));

                    var uptime = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
                    var uptimeFormatted = TimeSpan.toShortString((int)uptime.getSeconds());
                    messages.add(locale.get("stat.uptime", placeholderContext, Map.of(
                            "uptime", Component.nullToEmpty(uptimeFormatted)
                    )));

                    var maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
                    var allocatedMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                    var freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;

                    messages.add(locale.get("stat.maxMemory", placeholderContext, Map.of(
                            "memory", Component.nullToEmpty(String.valueOf(maxMemory)),
                            "hover", locale.get("stat.maxMemory.hover")
                    )));

                    messages.add(locale.get("stat.dedicatedMemory", placeholderContext, Map.of(
                            "memory", Component.nullToEmpty(String.valueOf(allocatedMemory)),
                            "hover", locale.get("stat.dedicatedMemory.hover")

                    )));

                    messages.add(locale.get("stat.freeMemory", placeholderContext, Map.of(
                            "memory", Component.nullToEmpty(String.valueOf(freeMemory)),
                            "hover", locale.get("stat.freeMemory.hover")
                    )));

                    var text = Component.empty();
                    text.append(locale.get("stat.title"));

                    for(var message : messages) {
                        text.append(Component.nullToEmpty("\n"));
                        text.append(message);
                    }

                    context.getSource().sendSuccess(() -> text, false);

                    return 1;
                });
    }
}
