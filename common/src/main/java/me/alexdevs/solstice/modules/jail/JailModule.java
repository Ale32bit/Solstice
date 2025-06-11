package me.alexdevs.solstice.modules.jail;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.events.CommandEvents;
import me.alexdevs.solstice.api.events.proxy.*;
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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JailModule extends ModuleBase.Toggleable {
    public static final String ID = "jail";

    public JailModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, JailConfig.class, JailConfig::new);
        Solstice.localeManager.registerModule(ID, JailLocale.MODULE);
        Solstice.playerData.registerData(ID, JailPlayerData.class, JailPlayerData::new);
        Solstice.serverData.registerData(ID, JailServerData.class, JailServerData::new);

        commands.add(new JailsCommand(this));
        commands.add(new JailCommand(this));
        commands.add(new UnjailCommand(this));
        commands.add(new CheckJailCommand(this));

        ProxyServerPlayConnectionEvents.JOIN.register((player, server) -> {
            Solstice.nextTick(() -> {
                var data = getPlayer(player.getUUID());

                if (data.jailed) {
                    sendToJail(player);
                } else if (data.teleportToPreviousLocation) {
                    unjailPlayer(player.getUUID());
                }
            });
        });

        ProxyServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, player, alive) -> {
            if (isPlayerJailed(player.getUUID())) {
                sendToJail(player);
            }
        });

        ProxyServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            Solstice.scheduler.scheduleAtFixedRate(this::checkJailedPlayers, 0, 1, TimeUnit.SECONDS);
        });

        CommandEvents.ALLOW_COMMAND.register((source, command) -> {
            if (!source.isPlayer())
                return true;

            if (isPlayerJailed(source.getPlayer().getUUID())) {
                var config = getConfig();
                var cmd = command.split(" ")[0];
                var canRun = config.allowedCommands.contains(cmd);
                if (!canRun) {
                    source.sendSuccess(() -> locale().get("cannotRunCommands"), false);
                }
                return canRun;
            }

            return true;
        });

        ProxyAttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            if (isPlayerJailed(player.getUUID())) {
                player.sendSystemMessage(locale().get("cannotBreakBlocks"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        ProxyAttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (isPlayerJailed(player.getUUID())) {
                player.sendSystemMessage(locale().get("cannotAttackEntities"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        ProxyPlayerBlockBreakEvents.BEFORE.register((world, player, blockPos, blockState, blockEntity) -> {
            if (isPlayerJailed(player.getUUID())) {
                player.sendSystemMessage(locale().get("cannotBreakBlocks"));
                return false;
            }

            return true;
        });

        ProxyUseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (isPlayerJailed(player.getUUID())) {
                player.sendSystemMessage(locale().get("cannotUseBlocks"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        ProxyUseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (isPlayerJailed(player.getUUID())) {
                player.sendSystemMessage(locale().get("cannotUseEntities"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        ProxyUseItemCallback.EVENT.register((player, world, hand) -> {
            var stack = player.getItemInHand(hand);
            if (isPlayerJailed(player.getUUID())) {
                player.sendSystemMessage(locale().get("cannotUseItems"));
                return InteractionResultHolder.fail(stack);
            }
            return InteractionResultHolder.pass(stack);
        });

        ProxyServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, parameters) -> {
            if (isPlayerJailed(player.getUUID())) {
                var config = getConfig();
                if (config.mute) {
                    player.sendSystemMessage(locale().get("cannotSpeak"));
                    return false;
                }
            }
            return true;
        });
    }

    private void checkJailedPlayers() {
        // run on server thread
        Solstice.nextTick(() -> {
            var players = Solstice.server.getPlayerList().getPlayers();
            for (var player : players) {
                var data = getPlayer(player.getUUID());
                if (isPlayerJailed(player.getUUID()) && data.jailTime > 0) {
                    if (data.jailedOn != null &&
                        data.jailedOn.getTime() + (data.jailTime * 1000L) < System.currentTimeMillis()) {
                        unjailPlayer(player.getUUID());
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

    public void sendToJail(ServerPlayer player) {
        Solstice.nextTick(() -> {
            var data = getPlayer(player.getUUID());
            var jails = getJails();
            var jail = jails.get(data.jailName);
            if (jail != null) {
                jail.teleport(player);

                var map = Map.of(
                        "player",
                        player.getName(),
                        "jail",
                        Component.nullToEmpty(data.jailName),
                        "duration",
                        Component.nullToEmpty(TimeSpan.toLongString(data.jailTime)),
                        "reason",
                        Component.nullToEmpty(data.jailReason)
                );

                Component text;
                if (data.jailTime > 0) {
                    if (data.jailReason != null) {
                        text = locale().get("playerJailedForWithReason", map);
                    } else {
                        text = locale().get("playerJailedFor", map);
                    }
                } else {
                    text = locale().get("playerJailed", map);
                }
                player.displayClientMessage(text, false);
            }
        });
    }

    public void unjailPlayer(UUID uuid) {
        var data = getPlayer(uuid);
        data.jailed = false;
        data.teleportToPreviousLocation = true;

        var player = Solstice.server.getPlayerList().getPlayer(uuid);
        if (player != null) {
            data.teleportToPreviousLocation = false;

            player.sendSystemMessage(locale().get("playerUnjailed"));

            if (data.previousLocation != null) {
                data.previousLocation.teleport(player);
            } else {
                var spawnModule = Solstice.modules.getModule(SpawnModule.class);
                spawnModule.getGlobalSpawnPosition().teleport(player);
            }
        }
    }
}
