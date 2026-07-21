package me.alexdevs.solstice.modules.powertool;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.utils.ResourceUtils;
import me.alexdevs.solstice.modules.powertool.commands.PowerToolCommand;
import me.alexdevs.solstice.modules.powertool.data.PowerToolLocale;
import me.alexdevs.solstice.modules.powertool.data.PowerToolPlayerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

//? >= 1.21.4
//import net.minecraft.server.level.ServerLevel;

//? < 1.21.4
import net.minecraft.world.InteractionResultHolder;


public class PowerToolModule extends ModuleBase {
    

    private CommandDispatcher<CommandSourceStack> dispatcher;

    public PowerToolModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerLocale(PowerToolLocale.MODULE);
        registerPlayerData(PowerToolPlayerData.class, PowerToolPlayerData::new);

        commands.add(new PowerToolCommand(this));

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> this.dispatcher = dispatcher);

        // USE
        UseItemCallback.EVENT.register((player, world, hand) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.USE)) {
                        var source = getSource(player);
                        execute(source, powertool.get(Action.USE), PlaceholderContext.of(player));

                        //? if >= 1.21.4
                        //return InteractionResult.CONSUME;
                        //? if < 1.21.4
                        return InteractionResultHolder.consume(stack);
                    }
                }
            }
            //? if >= 1.21.4
            //return InteractionResult.PASS;
            //? if < 1.21.4
            return InteractionResultHolder.pass(stack);
        });

        // ATTACK_BLOCK
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.ATTACK_BLOCK)) {
                        var source = getSource(player);
                        execute(source, powertool.get(Action.ATTACK_BLOCK), PlaceholderContext.of(player));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // ATTACK_ENTITY
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.ATTACK_ENTITY)) {
                        var source = getSource(player);
                        execute(source, powertool.get(Action.ATTACK_ENTITY), PlaceholderContext.of(entity));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // INTERACT_BLOCK
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.INTERACT_BLOCK)) {
                        var source = getSource(player);
                        execute(source, powertool.get(Action.INTERACT_BLOCK), PlaceholderContext.of(player));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        // INTERACT_ENTITY
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            var stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUUID());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.INTERACT_ENTITY)) {
                        var source = getSource(player);
                        execute(source, powertool.get(Action.INTERACT_ENTITY), PlaceholderContext.of(entity));

                        return InteractionResult.CONSUME;
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }

    private CommandSourceStack getSource(Player player) {
        //? >= 1.21.4
        //return player.createCommandSourceStackForNameResolution(((ServerLevel) player.level()));
        //? < 1.21.4
        return player.createCommandSourceStack();
    }

    public void execute(CommandSourceStack source, String command, PlaceholderContext context) {
        try {
            dispatcher.execute(resolveCommand(command, context), source);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.nullToEmpty(e.getMessage()));
        }
    }

    private String resolveCommand(String command, PlaceholderContext context) {
        //? if >= 26.1 {
        /*return Placeholders.COMMON_PLACEHOLDER_PARSER.parseNode(eu.pb4.placeholders.api.node.TextNode.convert(Component.nullToEmpty(command)))
                .toComponent(eu.pb4.placeholders.api.ParserContext.of(PlaceholderContext.COMMON_KEY, context)).getString();
        *///? } else {
        return Placeholders.parseText(Component.nullToEmpty(command), context).getString();
        //? }
    }

    public String getStackId(ItemStack stack) {
        //? if >= 26.1 {
        /*return ResourceUtils.identifier(stack.typeHolder().unwrapKey().get()).toString();
        *///? } elif >= 1.21.1 {
        return ResourceUtils.identifier(stack.getItemHolder().unwrapKey().get()).toString();
        //? }
    }

    public PowerToolPlayerData getData(UUID uuid) {
        return Solstice.playerData.get(uuid).getData(PowerToolPlayerData.class);
    }
}
