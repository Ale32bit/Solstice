package me.alexdevs.solstice.modules.tell.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.tell.TellModule;
import net.minecraft.commands.CommandSourceStack;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class ReplyCommand extends ModCommand<TellModule> {
    public ReplyCommand(TellModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("reply", "r");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var senderName = source.getTextName();
        var message = StringArgumentType.getString(context, "message");

        if (!module.lastSender.containsKey(senderName)) {
            var playerContext = PlaceholderContext.of(context.getSource());
            source.sendSuccess(() -> module.locale().get(
                    "noLastSenderReply",
                    playerContext
            ), false);
            return 1;
        }

        var targetName = module.lastSender.get(senderName);

        module.sendDirectMessage(targetName, source, message);

        return 1;
    }
}
