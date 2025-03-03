package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
    private final Map<UUID, Map<String, Integer>> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var playerUuid = handler.getPlayer().getUUID();
            cooldowns.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>());
        });

        Solstice.scheduler.scheduleAtFixedRate(this::tickDown, 0, 1, TimeUnit.SECONDS);
    }

    private void tickDown() {
        for (var entry : cooldowns.entrySet()) {
            for (var cdEntry : entry.getValue().entrySet()) {
                var val = cdEntry.getValue() - 1;
                if (val <= 0) {
                    entry.getValue().remove(cdEntry.getKey());
                } else {
                    cdEntry.setValue(val);
                }
            }
        }
    }

    public boolean isExempt(ServerPlayer player, String node) {
        return Permissions.check(player, node + ".exempt.cooldown", 3);
    }

    public boolean onCooldown(ServerPlayer player, String node) {
        if (isExempt(player, node))
            return false;
        var uuid = player.getUUID();
        var cooldown = cooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        return cooldown.getOrDefault(node, 0) > 0;
    }

    public Component getMessage(ServerPlayer player, String node) {
        var uuid = player.getUUID();
        var cooldown = cooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        var value = cooldown.getOrDefault(node, 0);
        var locale = Solstice.localeManager.getLocale(CoreModule.ID);
        return locale.get("~cooldown", Map.of(
                "timespan", Component.nullToEmpty(TimeSpan.toShortString(value))
        ));
    }

    /**
     * Check and start cooldown if the player is not on cooldown.
     * @param player Player
     * @param node Permission node
     * @param seconds Cooldown seconds
     * @return Whether to execute
     */
    public boolean trigger(ServerPlayer player, String node, int seconds) {
        if (onCooldown(player, node)) {
            return false;
        }

        if (isExempt(player, node)) {
            return true;
        }

        var uuid = player.getUUID();
        var cooldown = cooldowns.get(uuid);
        cooldown.put(node, seconds);

        return true;
    }

    public void clear(ServerPlayer player, String node) {
        var uuid = player.getUUID();
        var cooldown = cooldowns.get(uuid);
        cooldown.remove(node);
    }
}
