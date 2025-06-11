package me.alexdevs.solstice.api.permissions;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Simple option request event for {@link CommandSource}s.
 */
public interface OptionRequestEvent {

    Event<OptionRequestEvent> EVENT = EventFactory.createArrayBacked(
            OptionRequestEvent.class, (callbacks) -> (source, key) -> {
                for (OptionRequestEvent callback : callbacks) {
                    Optional<String> value = callback.onOptionRequest(source, key);
                    if (value.isPresent()) {
                        return value;
                    }
                }
                return Optional.empty();
            }
    );

    @NotNull Optional<String> onOptionRequest(@NotNull SharedSuggestionProvider source, @NotNull String key);
}

