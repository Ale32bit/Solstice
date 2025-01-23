package me.alexdevs.solstice.core.coreModule.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.Debug;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import me.alexdevs.solstice.api.text.Format;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class SolsticeCommand extends ModCommand<CoreModule> {
    public SolsticeCommand(CoreModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("solstice", "sol");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(context -> {
                    var modContainer = FabricLoader.getInstance().getModContainer(Solstice.MOD_ID).orElse(null);
                    if (modContainer == null) {
                        context.getSource().sendFeedback(() -> Text.of("Could not find self in mod list???"), false);
                        return 1;
                    }

                    var metadata = modContainer.getMetadata();
                    var placeholders = Map.of(
                            "name", Text.of(metadata.getName()),
                            "version", Text.of(metadata.getVersion().getFriendlyString())
                    );

                    var text = Format.parse(
                            "<gold>${name} v${version}</gold>",
                            placeholders);
                    context.getSource().sendFeedback(() -> text, false);

                    return 1;
                })
                .then(literal("reload")
                        .requires(require("reload", 3))
                        .executes(context -> {
                            try {
                                Solstice.configManager.loadData(true);
                                Solstice.localeManager.reload();
                            } catch (Exception e) {
                                Solstice.LOGGER.error("Failed to reload Solstice", e);
                                context.getSource().sendFeedback(() -> Text.of("Failed to load Solstice config. Check console for more info."), true);
                                return 1;
                            }

                            SolsticeEvents.RELOAD.invoker().onReload(Solstice.getInstance());

                            context.getSource().sendFeedback(() -> Text.of("Reloaded Solstice config"), true);

                            return 1;
                        }))
                .then(literal("debug")
                        .requires(require("debug", 4))
                        .then(literal("gen-command-list")
                                .executes(context -> {
                                    var builder = new StringBuilder();

                                    var list = new ArrayList<>(Debug.commandDebugList);

                                    list.sort(Comparator.comparing(Debug.CommandDebug::module));

                                    builder.append(String.format("| %s | %s | %s | %s |\n", "Module", "Command", "Aliases", "Permission"));
                                    builder.append("|---|---|---|---|\n");
                                    for (var command : list) {
                                        builder.append(String.format("| %s | %s | %s | %s |\n", command.module(), command.command(), String.join(" ", command.commands()), command.permission()));
                                    }

                                    var output = builder.toString();

                                    var file = FabricLoader.getInstance().getGameDir().resolve("solstice-commands.md").toFile();
                                    try (var fw = new FileWriter(file)) {
                                        fw.write(output);
                                    } catch (IOException e) {
                                        throw new SimpleCommandExceptionType(Text.of(e.getMessage())).create();
                                    }

                                    context.getSource().sendFeedback(() -> Text.of("Generated 'solstice-commands.md'"), true);

                                    return 1;
                                }))
                        .then(literal("tags")
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrThrow();

                                    var hand = player.getActiveHand();
                                    var itemStack = player.getStackInHand(hand);

                                    var entry = itemStack.getRegistryEntry().getKey().get();
                                    var entryString = String.format("Tags for [%s / %s]:", entry.getRegistry().toString(), entry.getValue().toString());

                                    var text = Text.empty();
                                    text.append(Text.of(entryString));
                                    var tags = itemStack.streamTags().iterator();
                                    while(tags.hasNext()) {
                                        var tag = tags.next();
                                        text.append(Text.of("\n"));
                                        text.append(
                                                Text.literal(" #" + tag.id().toString())
                                                        .setStyle(Style.EMPTY
                                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy")))
                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "#" + tag.id().toString()))
                                                        )
                                        );
                                    }

                                    context.getSource().sendFeedback(() -> text, false);

                                    return 1;
                                }))
                );
    }
}
