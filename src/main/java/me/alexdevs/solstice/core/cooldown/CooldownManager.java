package me.alexdevs.solstice.core.cooldown;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.events.ModuleCommandEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
    private final Map<UUID, Map<String, Integer>> cooldowns = new ConcurrentHashMap<>();

    // Map where configured node -> key of the group.
    // This map is replaced every reload and at start.
    private Map<String, String> nodesMap = Map.of();

    public CooldownManager() {
        Solstice.configManager.registerData("cooldown", CooldownConfig.class, CooldownConfig::new);

        SolsticeEvents.READY.register((instance, server) -> reloadNodes());
        SolsticeEvents.RELOAD.register(instance -> reloadNodes());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var playerUuid = handler.getPlayer().getUUID();
            cooldowns.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>());
        });

        Solstice.scheduler.scheduleAtFixedRate(this::tickDown, 0, 1, TimeUnit.SECONDS);

        ModuleCommandEvents.ALLOW_COMMAND.register((node, context, command) -> {
            if (!context.getSource().isPlayer())
                return true;

            Solstice.LOGGER.info("Node: {}", node);
            var setting = findSetting(node);
            if (setting.isEmpty()) {
                return true;
            }

            var key = setting.get().getKey();

            var player = context.getSource().getPlayer();
            var isOnCooldown = onCooldown(player, key);

            if (isOnCooldown) {
                var message = getMessage(player, key);
                context.getSource().sendFailure(message);
            }
            return !isOnCooldown;
        });

        ModuleCommandEvents.COMMAND.register((node, context, command) -> {
            if (!context.getSource().isPlayer())
                return;

            var setting = findSetting(node);
            if (setting.isEmpty()) {
                return;
            }

            trigger(context.getSource().getPlayer(), setting.get().getKey(), setting.get().cooldown);
        });
    }

    private void reloadNodes() {
        final var map = new HashMap<String, String>();
        for (var entry : getConfig().nodes.entrySet()) {
            var key = entry.getKey();
            var setting = entry.getValue();
            setting.setKey(key);
            for (var node : setting.nodes) {
                map.put(node, key);
            }
        }

        nodesMap = map;
    }

    public CooldownConfig getConfig() {
        return Solstice.configManager.getData(CooldownConfig.class);
    }

    private static String getParent(String node) {
        var chunks = node.split("\\.");

        if (chunks.length <= 1)
            return "";

        var sliced = Arrays.copyOf(chunks, chunks.length - 1);
        return String.join(".", sliced);
    }

    // node is path
    public Optional<CooldownSetting> findSetting(String originalNode) {
        // First try to get the exact match.
        if (nodesMap.containsKey(originalNode)) {
            var key = nodesMap.get(originalNode);
            return Optional.of(getConfig().nodes.get(key));
        }

        // Backtrack to find the wildcard, if it exists
        String parent;
        String node = originalNode;
        do {
            parent = getParent(node);
            var wildcard = parent + ".*";
            if (nodesMap.containsKey(wildcard)) {
                var key = nodesMap.get(wildcard);

                // Cache it for faster fetch later on
                nodesMap.put(originalNode, key);
                return Optional.of(getConfig().nodes.get(key));
            }
            node = parent;
        } while (!parent.isEmpty());

        return Optional.empty();
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
     *
     * @param player  Player
     * @param node    Permission node
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
