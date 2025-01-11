package me.alexdevs.solstice.modules.jail;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.events.CommandEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.jail.commands.CheckJailCommand;
import me.alexdevs.solstice.modules.jail.commands.JailCommand;
import me.alexdevs.solstice.modules.jail.commands.JailsCommand;
import me.alexdevs.solstice.modules.jail.commands.UnjailCommand;
import me.alexdevs.solstice.modules.jail.data.JailConfig;
import me.alexdevs.solstice.modules.jail.data.JailLocale;
import me.alexdevs.solstice.modules.jail.data.JailPlayerData;
import me.alexdevs.solstice.modules.jail.data.JailServerData;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JailModule extends ModuleBase {
    public static final String ID = "jail";

    public JailModule() {
        super(ID);

        Solstice.configManager.registerData(ID, JailConfig.class, JailConfig::new);
        Solstice.localeManager.registerModule(ID, JailLocale.MODULE);
        Solstice.playerData.registerData(ID, JailPlayerData.class, JailPlayerData::new);
        Solstice.serverData.registerData(ID, JailServerData.class, JailServerData::new);

        commands.add(new JailsCommand(this));
        commands.add(new JailCommand(this));
        commands.add(new UnjailCommand(this));
        commands.add(new CheckJailCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Solstice.nextTick(() -> {
                var data = getPlayer(handler.getPlayer().getUuid());
                if (data.jailed) {
                    sendToJail(handler.getPlayer());
                } else if (data.teleportToPreviousLocation) {
                    unjailPlayer(handler.getPlayer().getUuid());
                }
            });
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, player, alive) -> {
            if (isPlayerJailed(player.getUuid())) {
                sendToJail(player);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            Solstice.scheduler.scheduleAtFixedRate(this::checkJailedPlayers, 0, 1, TimeUnit.SECONDS);
        });

        CommandEvents.ALLOW_COMMAND.register((source, command) -> {
            if (!source.isExecutedByPlayer())
                return true;

            if (isPlayerJailed(source.getPlayer().getUuid())) {
                var config = getConfig();
                var cmd = command.split(" ")[0];
                var canRun = config.allowedCommands.contains(cmd);
                if (!canRun) {
                    source.sendFeedback(() -> locale().get("cannotRunCommands"), false);
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

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, parameters) -> {
            if (isPlayerJailed(player.getUuid())) {
                var config = getConfig();
                if(config.mute) {
                    player.sendMessage(locale().get("cannotSpeak"));
                    return false;
                }
            }
            return true;
        });
    }

    private void checkJailedPlayers() {
        // run on server thread
        Solstice.nextTick(() -> {
            var players = Solstice.server.getPlayerManager().getPlayerList();
            for (var player : players) {
                var data = getPlayer(player.getUuid());
                if (isPlayerJailed(player.getUuid()) && data.jailTime > 0) {
                    if (data.jailedOn != null && data.jailedOn.getTime() + (data.jailTime * 1000L) < System.currentTimeMillis()) {
                        unjailPlayer(player.getUuid());
                    }
                }
            }

        });
    }

    public JailConfig getConfig() {
        return Solstice.configManager.getData(JailConfig.class);
    }

    public Map<String, ServerLocation> getJails() {
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

                var map = Map.of(
                        "player", player.getName(),
                        "jail", Text.of(data.jailName),
                        "duration", Text.of(TimeSpan.toLongString(data.jailTime)),
                        "reason", Text.of(data.jailReason)
                );

                Text text;
                if (data.jailTime > 0) {
                    if (data.jailReason != null) {
                        text = locale().get("playerJailedForWithReason", map);
                    } else {
                        text = locale().get("playerJailedFor", map);
                    }
                } else {
                    text = locale().get("playerJailed", map);
                }
                player.sendMessage(text, false);
            }
        });
    }

    public void unjailPlayer(UUID uuid) {
        var data = getPlayer(uuid);
        data.jailed = false;
        data.teleportToPreviousLocation = true;

        var player = Solstice.server.getPlayerManager().getPlayer(uuid);
        if (player != null) {
            data.teleportToPreviousLocation = false;

            player.sendMessage(locale().get("playerUnjailed"));

            if (data.previousLocation != null) {
                data.previousLocation.teleport(player);
            } else {
                var spawnModule = Solstice.modules.getModule(SpawnModule.class);
                spawnModule.getGlobalSpawnPosition().teleport(player);
            }
        }
    }
}
