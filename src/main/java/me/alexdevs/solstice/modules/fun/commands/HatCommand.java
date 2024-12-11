package me.alexdevs.solstice.modules.fun.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.fun.FunModule;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class HatCommand extends ModCommand {
    private final Locale locale = Solstice.localeManager.getLocale(FunModule.ID);

    public HatCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("hat");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    var handStack = player.getMainHandStack();

                    if (handStack.isEmpty()) {
                        context.getSource().sendFeedback(() -> locale.get("hatEmptyStack"), false);
                        return 0;
                    }

                    var inventory = player.getInventory();
                    var oldHeadStack = inventory.armor.get(3); // head slot
                    inventory.setStack(inventory.selectedSlot, oldHeadStack.copyAndEmpty());
                    inventory.armor.set(3, handStack.copyAndEmpty());

                    context.getSource().sendFeedback(() -> locale.get("hatSuccess"), false);

                    return 1;
                });
    }
}
