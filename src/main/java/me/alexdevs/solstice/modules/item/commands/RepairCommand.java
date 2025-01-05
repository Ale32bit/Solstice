package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;


public class RepairCommand extends ModCommand<ItemModule> {
    public RepairCommand(ItemModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("repair", "repairitem");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require("repair", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    var item = player.getMainHandStack();

                    if(item.isEmpty()) {
                        context.getSource().sendFeedback(() -> module.locale().get("noItem"), false);
                        return 0;
                    }

                    if(!item.isDamageable()) {
                        context.getSource().sendFeedback(() -> module.locale().get("notRepairable"), false);
                        return 0;
                    }

                    if(item.isDamaged()) {
                        // Removes the tag altogether instead of just setting it to 0
                        item.remove(DataComponentTypes.DAMAGE);
                        context.getSource().sendFeedback(() -> module.locale().get("repaired"), false);
                        return 1;
                    } else {
                        context.getSource().sendFeedback(() -> module.locale().get("alreadyRepaired"), false);
                        return 0;
                    }
                });
    }
}
