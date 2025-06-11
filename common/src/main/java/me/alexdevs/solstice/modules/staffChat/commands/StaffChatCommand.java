package me.alexdevs.solstice.modules.staffChat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.staffChat.StaffChatModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class StaffChatCommand extends ModCommand<StaffChatModule> {
    public StaffChatCommand(StaffChatModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("staffchat", "sc");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(1))
                .executes(context -> {
                    var source = context.getSource();
                    var player = source.getPlayerOrException();
                    var enabled = module.toggleStaffChat(player.getUUID());
                    if (enabled) {
                        source.sendSuccess(() -> module.locale().get("enabled"), false);
                    } else {
                        source.sendSuccess(() -> module.locale().get("disabled"), false);
                    }
                    return 1;
                })
                .then(argument("message", MessageArgument.message())
                        .executes(context -> {
                            var message = MessageArgument.getMessage(context, "message");
                            module.sendStaffChatMessage(context.getSource().getDisplayName(), message);

                            return 1;
                        }));
    }
}
