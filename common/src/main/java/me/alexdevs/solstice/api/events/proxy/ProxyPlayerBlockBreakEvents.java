package me.alexdevs.solstice.api.events.proxy;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Contains server side events triggered by block breaking.
 */
public final class ProxyPlayerBlockBreakEvents {
    private ProxyPlayerBlockBreakEvents() {
    }

    /**
     * Callback before a block is broken.
     * Only called on the server; however, updates are synced with the client.
     *
     * <p>If any listener cancels a block breaking action, that block breaking
     * action is canceled and CANCELED event is fired. Otherwise, the
     * AFTER event is fired.</p>
     */
    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(
            Before.class, (listeners) -> (level, player, pos, state, entity) -> {
                for (Before event : listeners) {
                    boolean result = event.beforeBlockBreak(level, player, pos, state, entity);

                    if (!result) {
                        return false;
                    }
                }

                return true;
            }
    );

    @FunctionalInterface
    public interface Before {
        /**
         * Called before a block is broken and allows cancelling the block breaking.
         *
         * <p>Implementations should not modify the level or assume the block break has completed or failed.</p>
         *
         * @param level       the level in which the block is broken
         * @param player      the player breaking the block
         * @param pos         the position at which the block is broken
         * @param state       the block state <strong>before</strong> the block is broken
         * @param blockEntity the block entity <strong>before</strong> the block is broken, can be {@code null}
         *
         * @return {@code false} to cancel block breaking action, or {@code true} to pass to next listener
         */
        boolean beforeBlockBreak(
                Level level,
                Player player,
                BlockPos pos,
                BlockState state,
                @Nullable BlockEntity blockEntity
        );
    }
}
