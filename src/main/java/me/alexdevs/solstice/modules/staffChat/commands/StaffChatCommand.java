package me.alexdevs.solstice.modules.staffChat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.staffChat.StaffChatModule;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class StaffChatCommand extends ModCommand<StaffChatModule> {
    public StaffChatCommand(StaffChatModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("staffchat", "sc");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(1))
                .executes(context -> {
                    var source = context.getSource();
                    var player = source.getPlayerOrThrow();
                    var enabled = module.toggleStaffChat(player.getUuid());
                    if (enabled) {
                        source.sendFeedback(() -> module.locale().get("enabled"), false);
                    } else {
                        source.sendFeedback(() -> module.locale().get("disabled"), false);
                    }
                    return 1;
                })
                .then(argument("message", MessageArgumentType.message())
                        .executes(context -> {
                            var message = MessageArgumentType.getMessage(context, "message");
                            module.sendStaffChatMessage(context.getSource().getDisplayName(), message);

                            return 1;
                        }));
    }
}
