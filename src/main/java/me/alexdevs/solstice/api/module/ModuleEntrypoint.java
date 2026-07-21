package me.alexdevs.solstice.api.module;

import java.util.HashSet;

/**
 * Module provider for Solstice.
 *
 * <p>In {@code fabric.mod.json}, the entrypoint is defined with {@code solstice} key.</p>
 *
 * Provide a set of {@code ModuleBase} modules to register.
 *
 * @see ModuleBase
 * @see ModuleContainer
 */

@FunctionalInterface
public interface ModuleEntrypoint {
    HashSet<ModuleContainer<?>> register();
}
