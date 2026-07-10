package me.alexdevs.solstice.api.utils;


import org.jetbrains.annotations.Nullable;

import java.util.Objects;

//? < 1.21.11
import net.minecraft.resources.ResourceLocation;
//? >= 1.21.11
//import net.minecraft.resources.Identifier;

/**
 * Version-agnostic wrapper around Minecraft's {@code ResourceLocation}.
 * <p>
 * This is the <strong>only</strong> file in Solstice that imports
 * {@code net.minecraft.resources.ResourceLocation} or contains Stonecutter
 * conditionals for the MC resource-location API. When the underlying class
 * moves or is renamed in a future MC version, only this file needs updating.
 * <p>
 * Use {@link #get()} to pass the raw value to Minecraft APIs that are outside
 * Solstice's control.
 */
public final class SolsticeIdentifier implements Comparable<SolsticeIdentifier> {

    //? < 1.21.11
    private final ResourceLocation location;
    //? >= 1.21.11
    //private final Identifier location;

    //? < 1.21.11
    private SolsticeIdentifier(ResourceLocation location) {
    //? >= 1.21.11
    //private SolsticeIdentifier(Identifier location) {
        this.location = location;
    }

    // ---- Factory methods --------------------------------------------------------

    /**
     * Creates an identifier from a namespace and path.
     * Equivalent to {@code ResourceLocation.fromNamespaceAndPath} / {@code new ResourceLocation}.
     */
    public static SolsticeIdentifier of(String namespace, String path) {
        //? if >= 1.21.11 {
        /*return new SolsticeIdentifier(Identifier.fromNamespaceAndPath(namespace, path));
        *///? } elif >= 1.21.1 {
        return new SolsticeIdentifier(ResourceLocation.fromNamespaceAndPath(namespace, path));
        //? }

    }

    /**
     * Parses a {@code namespace:path} string, throwing on invalid input.
     */
    public static SolsticeIdentifier parse(String value) {
        //? if >= 1.21.11 {
        /*return new SolsticeIdentifier(Identifier.parse(value));
        *///? } elif >= 1.21.1 {
        return new SolsticeIdentifier(ResourceLocation.parse(value));
         //? }
    }

    /**
     * Parses a {@code namespace:path} string, returning {@code null} on invalid input.
     */
    public static @Nullable SolsticeIdentifier tryParse(String value) {
        //? < 1.21.11
        var loc = ResourceLocation.tryParse(value);
        //? >= 1.21.11
        //var loc = Identifier.tryParse(value);
        return loc != null ? new SolsticeIdentifier(loc) : null;
    }

    // ---- Delegation --------------------------------------------------------------

    public String getNamespace() {
        return location.getNamespace();
    }

    public String getPath() {
        return location.getPath();
    }

    /**
     * Returns a new {@code SolsticeIdentifier} with the same namespace but a different path.
     */
    public SolsticeIdentifier withPath(String path) {
        return new SolsticeIdentifier(location.withPath(path));
    }

    /**
     * Returns the underlying Minecraft {@code ResourceLocation}.
     * Only use this when passing to APIs outside Solstice's control.
     */
    //? < 1.21.11
    public ResourceLocation get() {
    //? >= 1.21.11
    //public Identifier get() {
        return location;
    }

    // ---- Object overrides --------------------------------------------------------

    @Override
    public String toString() {
        return location.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolsticeIdentifier that)) return false;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(location);
    }

    @Override
    public int compareTo(SolsticeIdentifier o) {
        return location.compareTo(o.location);
    }
}

