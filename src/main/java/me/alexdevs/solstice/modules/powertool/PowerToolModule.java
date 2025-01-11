package me.alexdevs.solstice.modules.powertool;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.powertool.commands.PowerToolCommand;
import me.alexdevs.solstice.modules.powertool.data.PowerToolLocale;
import me.alexdevs.solstice.modules.powertool.data.PowerToolPlayerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import java.util.UUID;

public class PowerToolModule extends ModuleBase {
    public static final String ID = "powertool";

    private CommandDispatcher<ServerCommandSource> dispatcher;

    public PowerToolModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, PowerToolLocale.MODULE);
        Solstice.playerData.registerData(ID, PowerToolPlayerData.class, PowerToolPlayerData::new);

        commands.add(new PowerToolCommand(this));

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            this.dispatcher = dispatcher;
        });

        // USE
        UseItemCallback.EVENT.register((player, world, hand) -> {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUuid());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.USE)) {
                        var source = player.getCommandSource();
                        execute(source, powertool.get(Action.USE), PlaceholderContext.of(player));

                        return TypedActionResult.consume(stack);
                    }
                }
            }
            return TypedActionResult.pass(stack);
        });

        // ATTACK_BLOCK
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUuid());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.ATTACK_BLOCK)) {
                        var source = player.getCommandSource();
                        execute(source, powertool.get(Action.ATTACK_BLOCK), PlaceholderContext.of(player));

                        return ActionResult.CONSUME;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // ATTACK_ENTITY
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUuid());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.ATTACK_ENTITY)) {
                        var source = player.getCommandSource();
                        execute(source, powertool.get(Action.ATTACK_ENTITY), PlaceholderContext.of(entity));

                        return ActionResult.CONSUME;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // INTERACT_BLOCK
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUuid());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.INTERACT_BLOCK)) {
                        var source = player.getCommandSource();
                        execute(source, powertool.get(Action.INTERACT_BLOCK), PlaceholderContext.of(player));

                        return ActionResult.CONSUME;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // INTERACT_ENTITY
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            var stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                var data = getData(player.getUuid());
                var itemId = getStackId(stack);
                if (data.powerTools.containsKey(itemId)) {
                    var powertool = data.powerTools.get(itemId);
                    if (powertool.containsKey(Action.INTERACT_ENTITY)) {
                        var source = player.getCommandSource();
                        execute(source, powertool.get(Action.INTERACT_ENTITY), PlaceholderContext.of(entity));

                        return ActionResult.CONSUME;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

    public void execute(ServerCommandSource source, String command, PlaceholderContext context) {
        try {
            dispatcher.execute(resolveCommand(command, context), source);
        } catch (CommandSyntaxException e) {
            source.sendError(Text.of(e.getMessage()));
        }
    }

    private String resolveCommand(String command, PlaceholderContext context) {
        return Placeholders.parseText(Text.of(command), context).getString();
    }

    public String getStackId(ItemStack stack) {
        return stack.getRegistryEntry().getKey().get().getValue().toString();
    }

    public PowerToolPlayerData getData(UUID uuid) {
        return Solstice.playerData.get(uuid).getData(PowerToolPlayerData.class);
    }
}
