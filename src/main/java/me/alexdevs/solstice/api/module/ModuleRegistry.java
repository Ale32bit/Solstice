package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.util.HashSet;
import java.util.function.Function;

public class ModuleRegistry {
    protected HashSet<ModuleBase> modules = new HashSet<>();
    protected final SolsticeIdentifier namespace;

    public ModuleRegistry(String namespace) {
        this.namespace = SolsticeIdentifier.of(namespace, "module");
    }

    public <T extends ModuleBase> T register(Function<SolsticeIdentifier, T> constructor, String path) {
        var module = constructor.apply(namespace.withPath(path));
        modules.add(module);
        return module;
    }

    public String getNamespace() {
        return namespace.toString();
    }

    public HashSet<ModuleBase> getModules() {
        return modules;
    }
}