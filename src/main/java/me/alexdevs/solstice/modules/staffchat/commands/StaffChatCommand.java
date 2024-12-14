package me.alexdevs.solstice.modules.staffchat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.staffchat.StaffChatModule;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class StaffChatCommand extends ModCommand {
    private final Locale locale = Solstice.localeManager.getLocale(StaffChatModule.ID);

    public StaffChatCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
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
                    var enabled = Solstice.modules.staffChat.toggleStaffChat(player.getUuid());
                    if (enabled) {
                        source.sendFeedback(() -> locale.get("enabled"), false);
                    } else {
                        source.sendFeedback(() -> locale.get("disabled"), false);
                    }
                    return 1;
                })
                .then(argument("message", MessageArgumentType.message())
                        .executes(context -> {
                            var message = MessageArgumentType.getMessage(context, "message");
                            Solstice.modules.staffChat.sendStaffChatMessage(context.getSource().getDisplayName(), message);

                            return 1;
                        }));
    }
}
