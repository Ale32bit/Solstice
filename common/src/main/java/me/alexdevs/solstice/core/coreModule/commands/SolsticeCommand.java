package me.alexdevs.solstice.core.coreModule.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.Debug;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.platform.PlatformHelper;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.literal;

public class SolsticeCommand extends ModCommand<CoreModule> {
    public SolsticeCommand(CoreModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("solstice", "sol");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name).requires(require(true)).executes(context -> {
            var modInfo = PlatformHelper.get().getModInfo(Solstice.MOD_ID);

            if (modInfo == null) {
                context.getSource()
                        .sendSuccess(() -> Component.nullToEmpty("Could not find self in mod list???"), false);
                return 1;
            }

            var placeholders = Map.of(
                    "name",
                    Component.nullToEmpty(modInfo.displayName()),
                    "version",
                    Component.nullToEmpty(modInfo.version())
            );

            var text = Format.parse("<gold>${name} v${version}</gold>", placeholders);
            context.getSource().sendSuccess(() -> text, false);

            return 1;
        }).then(literal("reload").requires(require("reload", 3)).executes(context -> {
            try {
                Solstice.configManager.loadData(true);
                Solstice.localeManager.reload();
            } catch (Exception e) {
                Solstice.LOGGER.error("Failed to reload Solstice", e);
                context.getSource().sendSuccess(
                        () -> Component.nullToEmpty("Failed to load Solstice config. Check console for more info."),
                        true
                );
                return 1;
            }

            SolsticeEvents.RELOAD.invoker().onReload(Solstice.getInstance());

            context.getSource().sendSuccess(() -> Component.nullToEmpty("Reloaded Solstice config"), true);

            return 1;
        })).then(literal("debug").requires(require("debug", 4)).then(literal("gen-command-list").executes(context -> {
            var builder = new StringBuilder();

            var list = new ArrayList<>(Debug.commandDebugList);

            list.sort(Comparator.comparing(Debug.CommandDebug::module));

            builder.append(String.format("| %s | %s | %s | %s |\n", "Module", "Command", "Aliases", "Permission"));
            builder.append("|---|---|---|---|\n");
            for (var command : list) {
                builder.append(String.format(
                        "| %s | %s | %s | %s |\n",
                        command.module(),
                        command.command(),
                        String.join(" ", command.commands()),
                        command.permission()
                ));
            }

            var output = builder.toString();
            var file = PlatformHelper.get().getGameDir().resolve("solstice-commands.md").toFile();

            try (var fw = new FileWriter(file)) {
                fw.write(output);
            } catch (IOException e) {
                throw new SimpleCommandExceptionType(Component.nullToEmpty(e.getMessage())).create();
            }

            context.getSource().sendSuccess(() -> Component.nullToEmpty("Generated 'solstice-commands.md'"), true);

            return 1;
        })).then(literal("tags").executes(context -> {
            var player = context.getSource().getPlayerOrException();

            var hand = player.getUsedItemHand();
            var itemStack = player.getItemInHand(hand);

            var entry = itemStack.getItemHolder().unwrapKey().get();
            var entryString = String.format("Tags for [%s / %s]:", entry.registry(), entry.location());

            var text = Component.empty();
            text.append(Component.nullToEmpty(entryString));
            var tags = itemStack.getTags().iterator();
            while (tags.hasNext()) {
                var tag = tags.next();
                text.append(Component.nullToEmpty("\n"));
                text.append(Component.literal(" #" + tag.location())
                        .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Component.nullToEmpty("Click to copy")
                                ))
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.COPY_TO_CLIPBOARD,
                                        "#" + tag.location()
                                ))));
            }

            context.getSource().sendSuccess(() -> text, false);

            return 1;
        })));
    }
}
