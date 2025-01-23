package me.alexdevs.solstice.integrations;

import me.alexdevs.solstice.Solstice;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

            eventBus.subscribe(container, UserDataRecalculateEvent.class, Listeners::onDataRecalculate);
        });
    }

    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("luckperms");
    }

    public static @Nullable String getPrefix(ServerPlayerEntity player) {
        if (!available) {
            return null;
        }

        return prefixMap.computeIfAbsent(player.getUuid(), uuid -> {
            var playerMeta = luckPerms.getPlayerAdapter(ServerPlayerEntity.class).getMetaData(player);
            return Optional.ofNullable(playerMeta.getPrefix());
        }).orElse(null);
    }

    public static @Nullable String getSuffix(ServerPlayerEntity player) {
        if (!available) {
            return null;
        }

        return suffixMap.computeIfAbsent(player.getUuid(), uuid -> {
            var playerMeta = luckPerms.getPlayerAdapter(ServerPlayerEntity.class).getMetaData(player);
            return Optional.ofNullable(playerMeta.getSuffix());
        }).orElse(null);
    }

    public static boolean isInGroup(ServerPlayerEntity player, String group) {
        if (!available) {
            return false;
        }
        var user = luckPerms.getPlayerAdapter(ServerPlayerEntity.class).getUser(player);
        var inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
        return inheritedGroups.stream().anyMatch(g -> g.getName().equalsIgnoreCase(group));
    }

    public static class Listeners {

        public static void onDataRecalculate(UserDataRecalculateEvent event) {
            var uuid = event.getUser().getUniqueId();
            prefixMap.remove(uuid);
            suffixMap.remove(uuid);
        }
    }
}
