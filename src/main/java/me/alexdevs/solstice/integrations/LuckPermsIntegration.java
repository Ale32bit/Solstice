package me.alexdevs.solstice.integrations;

import me.alexdevs.solstice.Solstice;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class LuckPermsIntegration {

    private static LuckPerms luckPerms;
    private static boolean available = false;

    public static void register() {

        var container = FabricLoader.getInstance().getModContainer(Solstice.MOD_ID).get();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            luckPerms = LuckPermsProvider.get();
            available = true;
            var eventBus = luckPerms.getEventBus();

            eventBus.subscribe(container, UserDataRecalculateEvent.class, Listeners::onDataRecalculate);
        });

    }

    public static @Nullable String getPrefix(ServerPlayerEntity player) {
        if(!available) {
            return null;
        }
        var playerMeta = luckPerms.getPlayerAdapter(ServerPlayerEntity.class).getMetaData(player);
        return playerMeta.getPrefix();
    }

    public static @Nullable String getSuffix(ServerPlayerEntity player) {
        if(!available) {
            return null;
        }
        var playerMeta = luckPerms.getPlayerAdapter(ServerPlayerEntity.class).getMetaData(player);
        return playerMeta.getSuffix();
    }

    public static class Listeners {

        public static void onDataRecalculate(UserDataRecalculateEvent event) {
            Solstice.modules.customName.refreshNames();
        }
    }
}
