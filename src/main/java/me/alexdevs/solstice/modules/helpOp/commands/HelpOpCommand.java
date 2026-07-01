package me.alexdevs.solstice.modules.helpOp.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.events.HelpOpCallback;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.helpOp.HelpOpModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HelpOpCommand extends ModCommand<HelpOpModule> {
    public HelpOpCommand(HelpOpModule module) {
        super(module);
    }


    @Override
    public List<String> getNames() {
        return List.of("helpop", "sos");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            var source = context.getSource();
                            source.getPlayerOrException(); // Ensure the command is run by a player
                            var sourceContext = PlaceholderContext.of(source);
                            var message = StringArgumentType.getString(context, "message");

                            var placeholders = Map.of(
                                    "message", Component.nullToEmpty(message)
                            );
                            var requestMessage = module.locale().get(
                                    "helpRequestMessage",
                                    sourceContext,
                                    placeholders

                            );
                            source.getServer().sendSystemMessage(requestMessage);

                            source.getServer().getPlayerList().getPlayers().forEach(player -> {
                                if (Permissions.check(player, getPermissionNode("operator"), 1)) {
                                    player.sendSystemMessage(requestMessage);
                                }
                            });

                            HelpOpCallback.EVENT.invoker().onHelpRequest(source.getPlayer(), message);

                            source.sendSuccess(() -> module.locale().get("helpRequestFeedback", sourceContext, placeholders), false);

                            return 1;
                        }));
    }
}
