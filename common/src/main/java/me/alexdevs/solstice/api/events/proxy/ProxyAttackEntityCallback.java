package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Callback for left-clicking ("attacking") an entity.
 * Is hooked in before the spectator check, so make sure to check for the player's game mode as well!
 *
 * <p>Upon return:
 * <ul><li>SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * <li>PASS falls back to further processing.
 * <li>FAIL cancels further processing and does not send a packet to the server.</ul>
 */
public interface ProxyAttackEntityCallback {
    Event<ProxyAttackEntityCallback> EVENT = EventFactory.createArrayBacked(
            ProxyAttackEntityCallback.class, (listeners) -> (player, level, hand, entity, hitResult) -> {
                for (ProxyAttackEntityCallback event : listeners) {
                    InteractionResult result = event.interact(player, level, hand, entity, hitResult);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            }
    );

    InteractionResult interact(
            Player player,
            Level level,
            InteractionHand hand,
            Entity entity,
            @Nullable EntityHitResult hitResult
    );
}
