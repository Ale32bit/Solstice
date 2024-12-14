package me.alexdevs.solstice.modules.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.util.Format;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class SolsticeCommand extends ModCommand {
    public SolsticeCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
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
                                Solstice.localeManager.load();
                            } catch (Exception e) {
                                Solstice.LOGGER.error("Failed to reload Solstice", e);
                                context.getSource().sendFeedback(() -> Text.of("Failed to load Solstice config. Check console for more info."), true);
                                return 1;
                            }

                            SolsticeEvents.RELOAD.invoker().onReload(Solstice.getInstance());

                            context.getSource().sendFeedback(() -> Text.of("Reloaded Solstice config"), true);

                            return 1;
                        }));
    }
}
