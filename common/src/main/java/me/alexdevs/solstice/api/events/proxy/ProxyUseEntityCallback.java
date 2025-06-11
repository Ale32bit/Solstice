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
 * Callback for right-clicking ("using") an entity.
 * Is hooked in before the spectator check, so make sure to check for the player's game mode as well!
 *
 * <p>On the logical client, the return values have the following meaning:
 * <ul>
 *     <li>SUCCESS cancels further processing, causes a hand swing, and sends a packet to the server.</li>
 *     <li>CONSUME cancels further processing, and sends a packet to the server. It does NOT cause a hand swing.</li>
 *     <li>PASS falls back to further processing.</li>
 *     <li>FAIL cancels further processing and does not send a packet to the server.</li>
 * </ul>
 *
 * <p>On the logical server, the return values have the following meaning:
 * <ul>
 *     <li>PASS falls back to further processing.</li>
 *     <li>Any other value cancels further processing.</li>
 * </ul>
 *
 * <p>Note that on the server, the {@link EntityHitResult} may be {@code null} if the client successfully interacted using
 * the {@linkplain Player#interactOn(Entity, InteractionHand) position-less overload}.
 * On the client, the {@link EntityHitResult} will never be null.
 */
public interface ProxyUseEntityCallback {
    Event<ProxyUseEntityCallback> EVENT = EventFactory.createArrayBacked(
            ProxyUseEntityCallback.class, (listeners) -> (player, level, hand, entity, hitResult) -> {
                for (ProxyUseEntityCallback event : listeners) {
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
