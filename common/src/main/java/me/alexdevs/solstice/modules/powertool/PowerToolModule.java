package me.alexdevs.solstice.modules.powertool;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.proxy.*;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.powertool.commands.PowerToolCommand;
import me.alexdevs.solstice.modules.powertool.data.PowerToolLocale;
import me.alexdevs.solstice.modules.powertool.data.PowerToolPlayerData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class PowerToolModule extends ModuleBase.Toggleable {
    public static final String ID = "powertool";

    private CommandDispatcher<CommandSourceStack> dispatcher;

    public PowerToolModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, PowerToolLocale.MODULE);
        Solstice.playerData.registerData(ID, PowerToolPlayerData.class, PowerToolPlayerData::new);

        commands.add(new PowerToolCommand(this));

        ProxyCommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> this.dispatcher = dispatcher);

        // USE
        ProxyUseItemCallback.EVENT.register((player, world, hand) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.USE)) {
                        var source = player.createCommandSourceStack();
                        execute(source, powertool.get(Action.USE), PlaceholderContext.of(player));

                        return InteractionResultHolder.consume(stack);
                    }
                }
            }
            return InteractionResultHolder.pass(stack);
        });

        // ATTACK_BLOCK
        ProxyAttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.ATTACK_BLOCK)) {
                        var source = player.createCommandSourceStack();
                        execute(source, powertool.get(Action.ATTACK_BLOCK), PlaceholderContext.of(player));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // ATTACK_ENTITY
        ProxyAttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.ATTACK_ENTITY)) {
                        var source = player.createCommandSourceStack();
                        execute(source, powertool.get(Action.ATTACK_ENTITY), PlaceholderContext.of(entity));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // INTERACT_BLOCK
        ProxyUseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.INTERACT_BLOCK)) {
                        var source = player.createCommandSourceStack();
                        execute(source, powertool.get(Action.INTERACT_BLOCK), PlaceholderContext.of(player));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // INTERACT_ENTITY
        ProxyUseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.INTERACT_ENTITY)) {
                        var source = player.createCommandSourceStack();
                        execute(source, powertool.get(Action.INTERACT_ENTITY), PlaceholderContext.of(entity));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }

    public void execute(CommandSourceStack source, String command, PlaceholderContext context) {
        try {
            dispatcher.execute(resolveCommand(command, context), source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.nullToEmpty(e.getMessage()));
        }
    }

    private String resolveCommand(String command, PlaceholderContext context) {
        return Placeholders.parseText(Component.nullToEmpty(command), context).getString();
    }

    public String getStackId(ItemStack stack) {
        return stack.getItemHolder().unwrapKey().get().location().toString();
    }

    public PowerToolPlayerData getData(UUID uuid) {
        return Solstice.playerData.get(uuid).getData(PowerToolPlayerData.class);
    }
}
