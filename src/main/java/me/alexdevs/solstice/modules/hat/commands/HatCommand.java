package me.alexdevs.solstice.modules.hat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.hat.HatModule;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class HatCommand extends ModCommand<HatModule> {
    public HatCommand(HatModule module) {
        super(module);
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
                        context.getSource().sendFeedback(() -> module.locale().get("emptyStack"), false);
                        return 0;
                    }

                    var config = module.getConfig();

                    var itemId = handStack.getRegistryEntry().getKey().get().getValue().toString();
                    var tags = handStack.streamTags();
                    if (config.whitelistFilter) {
                        if(!module.isInFilter(itemId) && !module.isInFilter(tags)) {
                            context.getSource().sendFeedback(() -> module.locale().get("notAllowed"), false);
                            return 0;
                        }
                    } else {
                        if(module.isInFilter(itemId) || module.isInFilter(tags)) {
                            context.getSource().sendFeedback(() -> module.locale().get("notAllowed"), false);
                            return 0;
                        }
                    }

                    //handStack.streamTags().toList().get(0).id().toString();

                    var inventory = player.getInventory();
                    var oldHeadStack = inventory.armor.get(3); // head slot
                    inventory.setStack(inventory.selectedSlot, oldHeadStack.copyAndEmpty());
                    inventory.armor.set(3, handStack.copyAndEmpty());

                    context.getSource().sendFeedback(() -> module.locale().get("success"), false);

                    return 1;
                });
    }
}
