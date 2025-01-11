package me.alexdevs.solstice.modules.kit;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.kit.commands.KitCommand;
import me.alexdevs.solstice.modules.kit.commands.KitsCommand;
import me.alexdevs.solstice.modules.kit.data.*;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class KitModule extends ModuleBase {
    public static final String ID = "kit";

    public KitModule() {
        super(ID);

        Solstice.configManager.registerData(ID, KitConfig.class, KitConfig::new);
        Solstice.localeManager.registerModule(ID, KitLocale.MODULE);
        Solstice.playerData.registerData(ID, KitPlayerData.class, KitPlayerData::new);
        Solstice.serverData.registerData(ID, KitServerData.class, KitServerData::new);

        commands.add(new KitCommand(this));
        commands.add(new KitsCommand(this));

        SolsticeEvents.WELCOME.register((player, server) -> {
            for (var kit : getKits().entrySet()) {
                if (kit.getValue().firstJoin) {
                    claimKit(player, kit.getKey());
                }
            }
        });
    }

    public Map<String, Kit> getKits() {
        return Solstice.serverData.getData(KitServerData.class).kits;
    }

    public boolean createKit(String name, List<ItemStack> items) {
        var kits = getKits();
        if (kits.containsKey(name)) {
            return false;
        }
        var kit = new Kit();
        kit.itemStacks = items.stream().map(Utils::serializeItemStack).toList();
        kits.put(name, kit);
        return true;
    }

    /**
     * Claim a kit regardless if the player could claim it.
     * Also flag the player as having it claimed.
     * @param player Player
     * @param name Kit name
     */
    public void claimKit(ServerPlayerEntity player, String name) {
        var playerData = Solstice.playerData.get(player).getData(KitPlayerData.class);
        var kit = getKits().get(name);
        var items = kit.getItemStacks();
        var inventory = player.getInventory();
        for (var stack : items) {
            inventory.insertStack(stack);
        }
        playerData.claimedKits.put(name, new Date());
    }

    /**
     * Check if a player has permission to claim a kit.
     * @param player Player
     * @param name Kit name
     * @return Whether the player has permission to claim the kit.
     */
    public boolean hasKitPermission(ServerPlayerEntity player, String name) {
        var config = Solstice.configManager.getData(KitConfig.class);
        if (config.requirePermission) {
            return Permissions.check(player, getPermissionNode("kits." + name), 2);
        } else {
            return Permissions.check(player, getPermissionNode("kits." + name), true);
        }
    }

    /**
     * Check if a player could technically claim the kit regardless of permission.
     * This method checks the oneTime flag and cooldown.
     * @param player Player
     * @param name Kit name
     * @return Whether the player could claim the kit.
     */
    public boolean couldClaimKit(ServerPlayerEntity player, String name) {
        var kit = getKits().get(name);
        var playerData = Solstice.playerData.get(player).getData(KitPlayerData.class);
        if (kit.oneTime && playerData.claimedKits.containsKey(name)) {
            return false;
        }

        if (kit.cooldownSeconds > 0) {
            if (playerData.claimedKits.containsKey(name)) {
                var startDate = playerData.claimedKits.get(name);
                var nowDate = new Date();

                var delta = (nowDate.getTime() - startDate.getTime()) / 1000;
                return delta >= kit.cooldownSeconds;
            }
        }

        return true;
    }

    public List<String> getPlayerKitNames(ServerPlayerEntity player) {
        return getKits().keySet().stream().filter(kit -> hasKitPermission(player, kit)).toList();
    }
}
