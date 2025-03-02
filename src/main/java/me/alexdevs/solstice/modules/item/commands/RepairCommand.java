package me.alexdevs.solstice.modules.item.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.item.ItemModule;
import net.minecraft.commands.CommandSourceStack;
import java.util.List;

import static net.minecraft.commands.Commands.literal;


public class RepairCommand extends ModCommand<ItemModule> {
    public RepairCommand(ItemModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("repair", "repairitem");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("repair", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();

                    var item = player.getMainHandItem();

                    if(item.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get("noItem"), false);
                        return 0;
                    }

                    if(!item.isDamageableItem()) {
                        context.getSource().sendSuccess(() -> module.locale().get("notRepairable"), false);
                        return 0;
                    }

                    if(item.isDamaged()) {
                        // Removes the NBT tag altogether instead of just setting it to 0
                        item.removeTagKey("Damage");
                        context.getSource().sendSuccess(() -> module.locale().get("repaired"), false);
                        return 1;
                    } else {
                        context.getSource().sendSuccess(() -> module.locale().get("alreadyRepaired"), false);
                        return 0;
                    }
                });
    }
}
