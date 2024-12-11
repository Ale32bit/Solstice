package me.alexdevs.solstice.modules.tell.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.tell.TellModule;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class ReplyCommand extends ModCommand {
    private final Locale locale = Solstice.localeManager.getLocale(TellModule.ID);

    public ReplyCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("reply", "r");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal("")
                .requires(require(true))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(this::execute));
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        var senderName = source.getName();
        var message = StringArgumentType.getString(context, "message");

        if (!Solstice.modules.tell.lastSender.containsKey(senderName)) {
            var playerContext = PlaceholderContext.of(context.getSource());
            source.sendFeedback(() -> locale.get(
                    "noLastSenderReply",
                    playerContext
            ), false);
            return 1;
        }

        var targetName = Solstice.modules.tell.lastSender.get(senderName);

        Solstice.modules.tell.sendDirectMessage(targetName, source, message);

        return 1;
    }
}
