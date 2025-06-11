package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Callback for right-clicking ("using") an item.
 * Is hooked in before the spectator check, so make sure to check for the player's game mode as well!
 *
 * <p>Upon return:
 * <ul><li>SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * <li>PASS falls back to further processing.
 * <li>FAIL cancels further processing and does not send a packet to the server.</ul>
 */
public interface ProxyUseItemCallback {
    Event<ProxyUseItemCallback> EVENT = EventFactory.createArrayBacked(
            ProxyUseItemCallback.class, listeners -> (player, level, hand) -> {
                for (ProxyUseItemCallback event : listeners) {
                    InteractionResultHolder<ItemStack> result = event.interact(player, level, hand);

                    if (result.getResult() != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResultHolder.pass(ItemStack.EMPTY);
            }
    );

    InteractionResultHolder<ItemStack> interact(Player player, Level level, InteractionHand hand);
}
