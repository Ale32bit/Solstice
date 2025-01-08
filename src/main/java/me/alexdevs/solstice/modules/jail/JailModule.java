package me.alexdevs.solstice.modules.jail;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.events.CommandEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.jail.commands.SetJailCommand;
import me.alexdevs.solstice.modules.jail.data.JailConfig;
import me.alexdevs.solstice.modules.jail.data.JailLocale;
import me.alexdevs.solstice.modules.jail.data.JailPlayerData;
import me.alexdevs.solstice.modules.jail.data.JailServerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import java.util.Map;
import java.util.UUID;

public class JailModule extends ModuleBase {
    public static final String ID = "jail";

    public JailModule() {
        super(ID);

        Solstice.configManager.registerData(ID, JailConfig.class, JailConfig::new);
        Solstice.localeManager.registerModule(ID, JailLocale.MODULE);
        Solstice.playerData.registerData(ID, JailPlayerData.class, JailPlayerData::new);
        Solstice.serverData.registerData(ID, JailServerData.class, JailServerData::new);

        commands.add(new SetJailCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Solstice.nextTick(() -> {
                if (isPlayerJailed(handler.getPlayer().getUuid())) {
                    sendToJail(handler.getPlayer());
                }
            });
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, player, alive) -> {
            if (isPlayerJailed(player.getUuid())) {
                sendToJail(player);
            }
        });

        CommandEvents.ALLOW_COMMAND.register((source, command) -> {
            if (!source.isExecutedByPlayer())
                return true;

            if (isPlayerJailed(source.getPlayer().getUuid())) {
                var config = getConfig();
                var cmd = command.split(" ")[0];
                var canRun = config.allowedCommands.contains(cmd);
                if (!canRun) {
                    source.sendFeedback(() -> locale().get("commandNotAllowed"), false);
                }
                return canRun;
            }

            return true;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            if (isPlayerJailed(player.getUuid())) {
                player.sendMessage(locale().get("cannotBreakBlocks"));
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (isPlayerJailed(player.getUuid())) {
                player.sendMessage(locale().get("cannotAttackEntities"));
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, blockPos, blockState, blockEntity) -> {
            if (isPlayerJailed(player.getUuid())) {
                player.sendMessage(locale().get("cannotBreakBlocks"));
                return false;
            }

            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (isPlayerJailed(player.getUuid())) {
                player.sendMessage(locale().get("cannotUseBlocks"));
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (isPlayerJailed(player.getUuid())) {
                player.sendMessage(locale().get("cannotUseEntities"));
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            var stack = player.getStackInHand(hand);
            if (isPlayerJailed(player.getUuid())) {
                player.sendMessage(locale().get("cannotUseItems"));
                return TypedActionResult.fail(stack);
            }
            return TypedActionResult.pass(stack);
        });
    }

    public JailConfig getConfig() {
        return Solstice.configManager.getData(JailConfig.class);
    }

    public Map<String, ServerPosition> getJails() {
        return Solstice.serverData.getData(JailServerData.class).jails;
    }

    public JailPlayerData getPlayer(UUID uuid) {
        return Solstice.playerData.get(uuid).getData(JailPlayerData.class);
    }

    public boolean isPlayerJailed(UUID uuid) {
        return getPlayer(uuid).jailed;
    }

    public void sendToJail(ServerPlayerEntity player) {
        Solstice.nextTick(() -> {
            var data = getPlayer(player.getUuid());
            var jails = getJails();
            var jail = jails.get(data.jailName);
            if (jail != null) {
                jail.teleport(player);
            }

        });
    }
}
