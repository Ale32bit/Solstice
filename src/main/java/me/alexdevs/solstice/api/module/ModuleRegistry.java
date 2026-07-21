package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.util.HashSet;
import java.util.function.Function;

public class ModuleRegistry {
    protected final SolsticeIdentifier id;
    protected final HashSet<ModuleContainer<?>> modules = new HashSet<>();

    public ModuleRegistry(String namespace) {
        this.id = SolsticeIdentifier.of(namespace, "");
    }

    public <T extends ModuleBase> ModuleContainer<T> register(String path, Function<ModuleProperties, T> module, ModuleProperties properties) {
        ModuleContainer<T> container = new ModuleContainer<>(this.id.withPath(path), module);
        modules.add(container);
        return container;
    }

    public <T extends ModuleBase> ModuleContainer<T> register(String path, Function<ModuleProperties, T> module) {
        return register(path, module, new ModuleProperties());
    }

    public HashSet<ModuleContainer<?>> getModules() {
        return modules;
    }
}
