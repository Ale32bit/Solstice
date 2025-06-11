package me.alexdevs.solstice.api.module;

import java.util.HashSet;

/**
 * Modules provider for Solstice.
 *
 * <p>In {@code fabric.mod.json}, the entrypoint is defined with {@code solstice} key.</p>
 *
 * Provide a set of {@code ModuleBase} modules to register.
 *
 * @see ModuleBase
 */

@FunctionalInterface
public interface ModuleEntrypoint {
    HashSet<ModuleBase> register();
}
