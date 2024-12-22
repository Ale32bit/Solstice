package me.alexdevs.solstice.modules.helpOp.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.helpOp.HelpOpModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HelpOpCommand extends ModCommand<HelpOpModule> {
    private final Locale locale = Solstice.localeManager.getLocale(HelpOpModule.ID);

    public HelpOpCommand(HelpOpModule module) {
        super(module);
    }


    @Override
    public List<String> getNames() {
        return List.of("helpop", "sos");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            var source = context.getSource();
                            var sourceContext = PlaceholderContext.of(source);
                            var message = StringArgumentType.getString(context, "message");

                            var placeholders = Map.of(
                                    "message", Text.of(message)
                            );
                            var requestMessage = locale.get(
                                    "helpRequestMessage",
                                    sourceContext,
                                    placeholders

                            );
                            source.getServer().sendMessage(requestMessage);

                            source.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                                if (Permissions.check(player, getPermissionNode("operator"), 1)) {
                                    player.sendMessage(requestMessage);
                                }
                            });

                            source.sendFeedback(() -> locale.get("helpRequestFeedback", sourceContext, placeholders), false);

                            return 1;
                        }));
    }
}
