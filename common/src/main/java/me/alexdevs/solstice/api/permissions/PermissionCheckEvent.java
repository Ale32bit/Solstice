package me.alexdevs.solstice.api.permissions;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Simple permissions check event for {@link CommandSource}s.
 */
public interface PermissionCheckEvent {

    Event<PermissionCheckEvent> EVENT = EventFactory.createArrayBacked(
            PermissionCheckEvent.class, (callbacks) -> (source, permission) -> {
                for (PermissionCheckEvent callback : callbacks) {
                    TriState state = callback.onPermissionCheck(source, permission);
                    if (state != TriState.DEFAULT) {
                        return state;
                    }
                }
                return TriState.DEFAULT;
            }
    );

    @NotNull TriState onPermissionCheck(@NotNull SharedSuggestionProvider source, @NotNull String permission);

}
