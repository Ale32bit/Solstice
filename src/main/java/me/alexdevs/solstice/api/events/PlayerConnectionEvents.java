package me.alexdevs.solstice.api.events;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class PlayerConnectionEvents {
    public static final Event<WhitelistBypass> WHITELIST_BYPASS = EventFactory.createArrayBacked(WhitelistBypass.class, callbacks ->
            (profile) -> {
                for (var callback : callbacks) {
                    if (callback.bypassWhitelist(profile))
                        return true;
                }
                return false;
            }
    );

    public static final Event<FullServerBypass> FULL_SERVER_BYPASS = EventFactory.createArrayBacked(FullServerBypass.class, callbacks ->
            (profile) -> {
                for (var callback : callbacks) {
                    if (callback.bypassFullServer(profile))
                        return true;
                }
                return false;
            }
    );

    @FunctionalInterface
    public interface WhitelistBypass {
        boolean bypassWhitelist(GameProfile profile);
    }

    @FunctionalInterface
    public interface FullServerBypass {
        boolean bypassFullServer(GameProfile profile);
    }
}
