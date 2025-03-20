package me.alexdevs.solstice.integrations;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.lucko.fabric.api.permissions.v0.OfflinePermissionCheckEvent;
import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.util.Tristate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LuckPermsIntegration {

    private static LuckPerms luckPerms;
    private static boolean available = false;

    private static final Map<UUID, Optional<String>> prefixMap = new ConcurrentHashMap<>();
    private static final Map<UUID, Optional<String>> suffixMap = new ConcurrentHashMap<>();

    public static void register() {
        if (!isAvailable()) {
            Solstice.LOGGER.warn("LuckPerms not available! It is recommended to install LuckPerms to configure permissions and groups.");
            return;
        }

        var container = FabricLoader.getInstance().getModContainer(Solstice.MOD_ID).get();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            luckPerms = LuckPermsProvider.get();
            available = true;
            var eventBus = luckPerms.getEventBus();

            if (!ConnectorIntegration.isForge()) {
                try {
                    eventBus.subscribe(container, UserDataRecalculateEvent.class, Listeners::onDataRecalculate);
                    eventBus.subscribe(container, NodeAddEvent.class, Listeners::onNodeAdded);
                    eventBus.subscribe(container, NodeRemoveEvent.class, Listeners::onNodeRemoved);
                    eventBus.subscribe(container, NodeClearEvent.class, Listeners::onNodeCleared);
                } catch(IllegalArgumentException e) {
                    // fallback
                    registerOnForge(eventBus);
                }
            } else {
                registerOnForge(eventBus);
            }
        });

        SolsticeEvents.RELOAD.register(event -> {
            prefixMap.clear();
            suffixMap.clear();
        });
    }

    private static void registerOnForge(EventBus eventBus) {
        eventBus.subscribe(UserDataRecalculateEvent.class, Listeners::onDataRecalculate);
        eventBus.subscribe(NodeAddEvent.class, Listeners::onNodeAdded);
        eventBus.subscribe(NodeRemoveEvent.class, Listeners::onNodeRemoved);
        eventBus.subscribe(NodeClearEvent.class, Listeners::onNodeCleared);

        Solstice.LOGGER.warn("Permissions API is not available. Solstice is now taking over!");

        // become the permissions api
        PermissionCheckEvent.EVENT.register((suggestion, permission) -> {
            var stack = (CommandSourceStack) suggestion;
            var entity = stack.getEntity();
            var result = checkPermission(entity, permission);

            return switch (result) {
                case TRUE -> TriState.TRUE;
                case FALSE -> TriState.FALSE;
                case UNDEFINED -> TriState.DEFAULT;
            };
        });

        OfflinePermissionCheckEvent.EVENT.register((uuid, permission) -> {
            var future = new CompletableFuture<TriState>();
            checkPermission(uuid, permission).thenAcceptAsync(result -> {
                future.complete(
                        switch (result) {
                            case TRUE -> TriState.TRUE;
                            case FALSE -> TriState.FALSE;
                            case UNDEFINED -> TriState.DEFAULT;
                        }
                );
            });
            return future;
        });
    }

    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("luckperms");
    }

    public static @Nullable String getPrefix(ServerPlayer player) {
        if (!available) {
            return null;
        }

        return prefixMap.computeIfAbsent(player.getUUID(), uuid -> {
            try {
                var playerMeta = luckPerms.getPlayerAdapter(ServerPlayer.class).getMetaData(player);
                return Optional.ofNullable(playerMeta.getPrefix());
            } catch (IllegalStateException e) {
                // Fake player may throw with IllegalStateException
                return Optional.empty();
            }
        }).orElse(null);
    }

    public static @Nullable String getSuffix(ServerPlayer player) {
        if (!available) {
            return null;
        }

        return suffixMap.computeIfAbsent(player.getUUID(), uuid -> {
            try {
                var playerMeta = luckPerms.getPlayerAdapter(ServerPlayer.class).getMetaData(player);
                return Optional.ofNullable(playerMeta.getSuffix());
            } catch (IllegalStateException e) {
                // Fake player may throw with IllegalStateException
                return Optional.empty();
            }
        }).orElse(null);
    }

    public static boolean isInGroup(ServerPlayer player, String group) {
        if (!available) {
            return false;
        }
        try {
            var user = luckPerms.getPlayerAdapter(ServerPlayer.class).getUser(player);
            var inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
            return inheritedGroups.stream().anyMatch(g -> g.getName().equalsIgnoreCase(group));
        } catch (IllegalStateException e) {
            // Fake player may throw with IllegalStateException
            return false;
        }
    }

    public static Tristate checkPermission(Entity entity, String permission) {
        if (!(entity instanceof ServerPlayer)) {
            return Tristate.UNDEFINED;
        }
        var user = luckPerms.getPlayerAdapter(ServerPlayer.class).getUser((ServerPlayer) entity);
        var perms = user.getCachedData().getPermissionData();
        return perms.checkPermission(permission);
    }

    public static CompletableFuture<Tristate> checkPermission(UUID uuid, String permission) {
        var future = new CompletableFuture<Tristate>();
        if (luckPerms.getUserManager().isLoaded(uuid)) {
            var user = luckPerms.getUserManager().getUser(uuid);
            if (user == null) {
                future.complete(Tristate.UNDEFINED);
            } else {
                future.complete(user
                        .getCachedData()
                        .getPermissionData()
                        .checkPermission(permission));
            }
        } else {
            luckPerms.getUserManager().loadUser(uuid).thenAccept(user -> future
                    .complete(user
                            .getCachedData()
                            .getPermissionData()
                            .checkPermission(permission)));
        }
        return future;
    }

    public static void clearUserCache(UUID uuid) {
        prefixMap.remove(uuid);
        suffixMap.remove(uuid);
    }

    public static void recalculateUsersCache() {
        Solstice.server.getPlayerList().getPlayers().forEach(player -> {
            getPrefix(player);
            getSuffix(player);
        });
    }

    public static class Listeners {
        public static void onDataRecalculate(UserDataRecalculateEvent event) {
            var uuid = event.getUser().getUniqueId();
            clearUserCache(uuid);
        }

        // scheduled with delay because it would not update otherwise

        public static void onNodeAdded(NodeAddEvent event) {
            if (event.isGroup()) {
                Solstice.scheduler.scheduleSync(() -> {
                    if (event.getNode().getType() == NodeType.PREFIX) {
                        prefixMap.clear();
                    } else if (event.getNode().getType() == NodeType.SUFFIX) {
                        suffixMap.clear();
                    }

                    recalculateUsersCache();
                }, 50, TimeUnit.MILLISECONDS);
            }
        }

        public static void onNodeRemoved(NodeRemoveEvent event) {
            if (event.isGroup()) {
                Solstice.scheduler.scheduleSync(() -> {
                    if (event.getNode().getType() == NodeType.PREFIX) {
                        prefixMap.clear();
                    } else if (event.getNode().getType() == NodeType.SUFFIX) {
                        suffixMap.clear();
                    }

                    recalculateUsersCache();
                }, 50, TimeUnit.MILLISECONDS);
            }
        }

        public static void onNodeCleared(NodeClearEvent event) {
            if (event.isGroup()) {
                Solstice.scheduler.scheduleSync(() -> {
                    prefixMap.clear();
                    suffixMap.clear();

                    recalculateUsersCache();
                }, 50, TimeUnit.MILLISECONDS);
            }
        }
    }
}
