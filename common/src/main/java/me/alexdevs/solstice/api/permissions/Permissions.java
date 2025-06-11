package me.alexdevs.solstice.api.permissions;

import com.mojang.authlib.GameProfile;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * A simple permissions API.
 */
public interface Permissions {

    /**
     * Gets the {@link TriState state} of a {@code permission} for the given source.
     *
     * @param source     the source
     * @param permission the permission
     *
     * @return the state of the permission
     */
    static @NotNull TriState getPermissionValue(@NotNull SharedSuggestionProvider source, @NotNull String permission) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(permission, "permission");
        return PermissionCheckEvent.EVENT.invoker().onPermissionCheck(source, permission);
    }

    /**
     * Performs a permission check, falling back to the {@code defaultValue} if the resultant
     * state is {@link TriState#DEFAULT}.
     *
     * @param source       the source to perform the check for
     * @param permission   the permission to check
     * @param defaultValue the default value to use if nothing has been set
     *
     * @return the result of the permission check
     */
    static boolean check(@NotNull SharedSuggestionProvider source, @NotNull String permission, boolean defaultValue) {
        return getPermissionValue(source, permission).orElse(defaultValue);
    }

    /**
     * Performs a permission check, falling back to requiring the {@code defaultRequiredLevel}
     * if the resultant state is {@link TriState#DEFAULT}.
     *
     * @param source               the source to perform the check for
     * @param permission           the permission to check
     * @param defaultRequiredLevel the required permission level to check for as a fallback
     *
     * @return the result of the permission check
     */
    static boolean check(
            @NotNull SharedSuggestionProvider source,
            @NotNull String permission,
            int defaultRequiredLevel
    ) {
        return getPermissionValue(source, permission).orElseGet(() -> source.hasPermission(defaultRequiredLevel));
    }

    /**
     * Performs a permission check, falling back to {@code false} if the resultant state
     * is {@link TriState#DEFAULT}.
     *
     * @param source     the source to perform the check for
     * @param permission the permission to check
     *
     * @return the result of the permission check
     */
    static boolean check(@NotNull SharedSuggestionProvider source, @NotNull String permission) {
        return getPermissionValue(source, permission).orElse(false);
    }

    /**
     * Creates a predicate which returns the result of performing a permission check,
     * falling back to the {@code defaultValue} if the resultant state is {@link TriState#DEFAULT}.
     *
     * @param permission   the permission to check
     * @param defaultValue the default value to use if nothing has been set
     *
     * @return a predicate that will perform the permission check
     */
    static @NotNull Predicate<CommandSourceStack> require(@NotNull String permission, boolean defaultValue) {
        Objects.requireNonNull(permission, "permission");
        return player -> check(player, permission, defaultValue);
    }

    /**
     * Creates a predicate which returns the result of performing a permission check,
     * falling back to requiring the {@code defaultRequiredLevel} if the resultant state is
     * {@link TriState#DEFAULT}.
     *
     * @param permission           the permission to check
     * @param defaultRequiredLevel the required permission level to check for as a fallback
     *
     * @return a predicate that will perform the permission check
     */
    static @NotNull Predicate<CommandSourceStack> require(@NotNull String permission, int defaultRequiredLevel) {
        Objects.requireNonNull(permission, "permission");
        return player -> check(player, permission, defaultRequiredLevel);
    }

    /**
     * Creates a predicate which returns the result of performing a permission check,
     * falling back to {@code false} if the resultant state is {@link TriState#DEFAULT}.
     *
     * @param permission the permission to check
     *
     * @return a predicate that will perform the permission check
     */
    static @NotNull Predicate<CommandSourceStack> require(@NotNull String permission) {
        Objects.requireNonNull(permission, "permission");
        return player -> check(player, permission);
    }

    /**
     * Gets the {@link TriState state} of a {@code permission} for the given entity.
     *
     * @param entity     the entity
     * @param permission the permission
     *
     * @return the state of the permission
     */
    static @NotNull TriState getPermissionValue(@NotNull Entity entity, @NotNull String permission) {
        Objects.requireNonNull(entity, "entity");
        return getPermissionValue(Util.commandSourceFromEntity(entity), permission);
    }

    /**
     * Performs a permission check, falling back to the {@code defaultValue} if the resultant
     * state is {@link TriState#DEFAULT}.
     *
     * @param entity       the entity to perform the check for
     * @param permission   the permission to check
     * @param defaultValue the default value to use if nothing has been set
     *
     * @return the result of the permission check
     */
    static boolean check(@NotNull Entity entity, @NotNull String permission, boolean defaultValue) {
        Objects.requireNonNull(entity, "entity");
        return check(Util.commandSourceFromEntity(entity), permission, defaultValue);
    }

    /**
     * Performs a permission check, falling back to requiring the {@code defaultRequiredLevel}
     * if the resultant state is {@link TriState#DEFAULT}.
     *
     * @param entity               the entity to perform the check for
     * @param permission           the permission to check
     * @param defaultRequiredLevel the required permission level to check for as a fallback
     *
     * @return the result of the permission check
     */
    static boolean check(@NotNull Entity entity, @NotNull String permission, int defaultRequiredLevel) {
        Objects.requireNonNull(entity, "entity");
        return check(Util.commandSourceFromEntity(entity), permission, defaultRequiredLevel);
    }

    /**
     * Performs a permission check, falling back to {@code false} if the resultant state
     * is {@link TriState#DEFAULT}.
     *
     * @param entity     the entity to perform the check for
     * @param permission the permission to check
     *
     * @return the result of the permission check
     */
    static boolean check(@NotNull Entity entity, @NotNull String permission) {
        Objects.requireNonNull(entity, "entity");
        return check(Util.commandSourceFromEntity(entity), permission);
    }

    /**
     * Gets the {@link TriState state} of a {@code permission} for the given (potentially) offline player.
     *
     * @param uuid       the uuid of the player
     * @param permission the permission
     *
     * @return the state of the permission
     */
    static @NotNull CompletableFuture<TriState> getPermissionValue(@NotNull UUID uuid, @NotNull String permission) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(permission, "permission");
        return OfflinePermissionCheckEvent.EVENT.invoker().onPermissionCheck(uuid, permission);
    }

    /**
     * Performs a permission check, falling back to the {@code defaultValue} if the resultant
     * state is {@link TriState#DEFAULT}.
     *
     * @param uuid         the uuid of the player to perform the check for
     * @param permission   the permission to check
     * @param defaultValue the default value to use if nothing has been set
     *
     * @return the result of the permission check
     */
    static CompletableFuture<Boolean> check(@NotNull UUID uuid, @NotNull String permission, boolean defaultValue) {
        return getPermissionValue(uuid, permission).thenApplyAsync(state -> state.orElse(defaultValue));
    }

    /**
     * Performs a permission check, falling back to {@code false} if the resultant state
     * is {@link TriState#DEFAULT}.
     *
     * @param uuid       the uuid of the player to perform the check for
     * @param permission the permission to check
     *
     * @return the result of the permission check
     */
    static CompletableFuture<Boolean> check(@NotNull UUID uuid, @NotNull String permission) {
        return getPermissionValue(uuid, permission).thenApplyAsync(state -> state.orElse(false));
    }

    /**
     * Performs a permission check, falling back to {@code false} if the resultant state
     * is {@link TriState#DEFAULT}.
     *
     * @param profile      the player profile to perform the check for
     * @param permission   the permission to check
     * @param defaultValue the default value to use if nothing has been set
     *
     * @return the result of the permission check
     */
    static CompletableFuture<Boolean> check(
            @NotNull GameProfile profile,
            @NotNull String permission,
            boolean defaultValue
    ) {
        Objects.requireNonNull(profile, "profile");
        return check(profile.getId(), permission, defaultValue);
    }

    /**
     * Performs a permission check, falling back to {@code false} if the resultant state
     * is {@link TriState#DEFAULT}.
     *
     * @param profile    the player profile to perform the check for
     * @param permission the permission to check
     *
     * @return the result of the permission check
     */
    static CompletableFuture<Boolean> check(@NotNull GameProfile profile, @NotNull String permission) {
        Objects.requireNonNull(profile, "profile");
        return check(profile.getId(), permission);
    }

    /**
     * Performs a permission check, falling back to requiring the {@code defaultRequiredLevel}
     * if the resultant state is {@link TriState#DEFAULT}.
     *
     * @param profile              the player profile to perform the check for
     * @param permission           the permission to check
     * @param defaultRequiredLevel the required permission level to check for as a fallback
     * @param server               instance to check permission level
     *
     * @return the result of the permission check
     */
    static CompletableFuture<Boolean> check(
            @NotNull GameProfile profile,
            @NotNull String permission,
            int defaultRequiredLevel,
            @NotNull MinecraftServer server
    ) {
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(server, "server");
        BooleanSupplier permissionLevelCheck = () -> server.getProfilePermissions(profile) >= defaultRequiredLevel;
        return getPermissionValue(profile.getId(), permission).thenApplyAsync(state -> state.orElseGet(
                permissionLevelCheck));
    }

}
