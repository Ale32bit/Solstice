package me.alexdevs.solstice.modules.admin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.PlayerConnectionEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.resources.ResourceLocation;

public class AdminModule extends ModuleBase {
    public AdminModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        PlayerConnectionEvents.WHITELIST_BYPASS.register(profile -> {
            try {
                return Permissions.check(profile, getWhitelistBypassPermission(), false).get();
            } catch (Exception e) {
                Solstice.LOGGER.error("Error checking whitelist bypass for profile {}", profile.getId(), e);
            }
            return false;
        });

        PlayerConnectionEvents.FULL_SERVER_BYPASS.register(profile -> {
            try {
                return Permissions.check(profile, getFullServerBypassPermission(), false).get();
            } catch (Exception e) {
                Solstice.LOGGER.error("Error checking full server bypass for profile {}", profile.getId(), e);
            }
            return false;
        });
    }

    public String getWhitelistBypassPermission() {
        return getPermissionNode("bypass.whitelist");
    }

    public String getFullServerBypassPermission() {
        return getPermissionNode("bypass.fullserver");
    }
}
