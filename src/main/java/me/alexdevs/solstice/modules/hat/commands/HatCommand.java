package me.alexdevs.solstice.modules.hat.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.ResourceUtils;
import me.alexdevs.solstice.modules.hat.HatModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class HatCommand extends ModCommand<HatModule> {
    public HatCommand(HatModule module) {
        super(module);
    }


    @Override
    public List<String> getNames() {
        return List.of("hat");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var handStack = player.getMainHandItem();

                    if (handStack.isEmpty()) {
                        context.getSource().sendSuccess(() -> module.locale().get("emptyStack"), false);
                        return 0;
                    }

                    var config = module.getConfig();

                    //? if >= 26.1 {
                    /*var itemId = ResourceUtils.identifier(handStack.typeHolder().unwrapKey().get()).toString();
                    var tags = handStack.typeHolder().tags();
                    *///? } else {
                    var itemId = ResourceUtils.identifier(handStack.getItemHolder().unwrapKey().get()).toString();
                    var tags = handStack.getTags();
                    //? }


                    if (config.whitelistFilter) {
                        if(!module.isInFilter(itemId) && !module.isInFilter(tags)) {
                            context.getSource().sendSuccess(() -> module.locale().get("notAllowed"), false);
                            return 0;
                        }
                    } else {
                        if(module.isInFilter(itemId) || module.isInFilter(tags)) {
                            context.getSource().sendSuccess(() -> module.locale().get("notAllowed"), false);
                            return 0;
                        }
                    }

                    //handStack.streamTags().toList().get(0).id().toString();

                    var inventory = player.getInventory();

                    //? if < 1.21.11 {
                    var oldHeadStack = inventory.armor.get(3).copyAndClear(); // head slot
                    var newHeadStack = handStack.copyAndClear();
                    inventory.setItem(inventory.selected, oldHeadStack);
                    inventory.armor.set(3, newHeadStack);
                    //? } else {
                    /*var oldHeadStack = player.getItemBySlot(EquipmentSlot.HEAD);
                    var newHeadStack = handStack.copyAndClear();
                    inventory.setItem(inventory.getSelectedSlot(), oldHeadStack.copyAndClear());
                    player.setItemSlot(EquipmentSlot.HEAD, newHeadStack);
                    *///? }

                    context.getSource().sendSuccess(() -> module.locale().get("success"), false);

                    return 1;
                });
    }
}
