package me.alexdevs.solstice.api.permissions;

import me.alexdevs.solstice.api.events.backend.Event;
import me.alexdevs.solstice.api.events.backend.EventFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Simple option request event for (potentially) offline players.
 */
public interface OfflineOptionRequestEvent {

    Event<OfflineOptionRequestEvent> EVENT = EventFactory.createArrayBacked(
            OfflineOptionRequestEvent.class, (callbacks) -> (uuid, key) -> {
                CompletableFuture<Optional<String>> res = CompletableFuture.completedFuture(null);
                for (OfflineOptionRequestEvent callback : callbacks) {
                    res = res.thenCompose(value -> {
                        if (value.isPresent()) {
                            return CompletableFuture.completedFuture(value);
                        }
                        return callback.onOptionRequest(uuid, key);
                    });
                }
                return res;
            }
    );

    @NotNull CompletableFuture<Optional<String>> onOptionRequest(@NotNull UUID uuid, @NotNull String key);

}
