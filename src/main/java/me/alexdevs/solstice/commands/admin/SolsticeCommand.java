package me.alexdevs.solstice.commands.admin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.CustomNameFormat;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class SolsticeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("solstice")
                .requires(Permissions.require("solstice.command.solstice", 3))
                .executes(context -> {
                    var modContainer = FabricLoader.getInstance().getModContainer(me.alexdevs.solstice.Solstice.MOD_ID).orElse(null);
                    if(modContainer == null) {
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
                        .requires(Permissions.require("solstice.command.solstice.reload", 3))
                        .executes(context -> {
                            try {
                                Solstice.configManager.load();
                                Solstice.localeManager.load();

                                CustomNameFormat.refreshNames();
                            } catch (Exception e) {
                                Solstice.LOGGER.error("Failed to reload Solstice", e);
                                context.getSource().sendFeedback(() -> Text.of("Failed to load Solstice config. Check console for more info."), true);
                                return 1;
                            }

                            SolsticeEvents.RELOAD.invoker().onReload(Solstice.getInstance());

                            context.getSource().sendFeedback(() -> Text.of("Reloaded Solstice config"), true);

                            return 1;
                        }));

        var node = dispatcher.register(rootCommand);
        dispatcher.register(literal("sol").redirect(node));
    }
}
