package me.alexdevs.solstice.modules.jail;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.events.CommandEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ModuleProvider;
import me.alexdevs.solstice.modules.jail.commands.CheckJailCommand;
import me.alexdevs.solstice.modules.jail.commands.JailCommand;
import me.alexdevs.solstice.modules.jail.commands.JailsCommand;
import me.alexdevs.solstice.modules.jail.commands.UnjailCommand;
import me.alexdevs.solstice.modules.jail.data.JailConfig;
import me.alexdevs.solstice.modules.jail.data.JailLocale;
import me.alexdevs.solstice.modules.jail.data.JailPlayerData;
import me.alexdevs.solstice.modules.jail.data.JailServerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//? < 1.21.4
import net.minecraft.world.InteractionResultHolder;

public class JailModule extends ModuleBase.Toggleable {
    

    public JailModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(JailConfig.class, JailConfig::new);
        registerLocale(JailLocale.MODULE);
        registerPlayerData(JailPlayerData.class, JailPlayerData::new);
        registerServerData(JailServerData.class, JailServerData::new);

        commands.add(new JailsCommand(this));
        commands.add(new JailCommand(this));
        commands.add(new UnjailCommand(this));
        commands.add(new CheckJailCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Solstice.nextTick(() -> {
                var data = getPlayer(handler.getPlayer().getUUID());
                if (data.jailed) {
                    sendToJail(handler.getPlayer());
                } else if (data.teleportToPreviousLocation) {
                    unjailPlayer(handler.getPlayer().getUUID());
                }
            });
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, player, alive) -> {
            if (isPlayerJailed(player.getUUID())) {
                sendToJail(player);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
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

        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            if (isPlayerJailed(player.getUUID())) {
                tryNotifyPlayer(player, locale().get("cannotBreakBlocks"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (isPlayerJailed(player.getUUID())) {
                tryNotifyPlayer(player, locale().get("cannotAttackEntities"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, blockPos, blockState, blockEntity) -> {
            if (isPlayerJailed(player.getUUID())) {
                tryNotifyPlayer(player, locale().get("cannotBreakBlocks"));
                return false;
            }

            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (isPlayerJailed(player.getUUID())) {
                tryNotifyPlayer(player, locale().get("cannotUseBlocks"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (isPlayerJailed(player.getUUID())) {
                tryNotifyPlayer(player, locale().get("cannotUseEntities"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            //? if >= 1.21.4 {
            /*if (isPlayerJailed(player.getUUID()) && player instanceof ServerPlayer) {
                ((ServerPlayer) player).sendSystemMessage(locale().get("cannotUseItems"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
            *///? } else {
            var stack = player.getItemInHand(hand);
            if (isPlayerJailed(player.getUUID())) {
                player.sendSystemMessage(locale().get("cannotUseItems"));
                return InteractionResultHolder.fail(stack);
            }
            return InteractionResultHolder.pass(stack);
            //? }
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((signedMessage, player, parameters) -> {
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
                    if (data.jailedOn != null && data.jailedOn.getTime() + (data.jailTime * 1000L) < System.currentTimeMillis()) {
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

    private void tryNotifyPlayer(Player player, Component message) {
        //? >= 1.21.4
        //if (player instanceof ServerPlayer sp) sp.sendSystemMessage(message);
        //? < 1.21.4
        player.sendSystemMessage(message);
    }

    public void sendToJail(ServerPlayer player) {
        Solstice.nextTick(() -> {
            var data = getPlayer(player.getUUID());
            var jails = getJails();
            var jail = jails.get(data.jailName);
            if (jail != null) {
                jail.teleport(player);

                var map = Map.of(
                        "player", player.getName(),
                        "jail", Component.nullToEmpty(data.jailName),
                        "duration", Component.nullToEmpty(TimeSpan.toLongString(data.jailTime)),
                        "reason", Component.nullToEmpty(data.jailReason)
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
                ModuleProvider.SPAWN.getGlobalSpawnPosition().teleport(player);
            }
        }
    }
}
