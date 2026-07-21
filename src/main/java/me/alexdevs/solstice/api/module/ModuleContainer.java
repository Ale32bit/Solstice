package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.util.function.Function;

public class ModuleContainer<T extends ModuleBase> {
    private final SolsticeIdentifier id;
    private final Function<ModuleProperties, T> module;
    private final ModuleProperties properties;

    private T instance = null;

    public ModuleContainer(SolsticeIdentifier id, Function<ModuleProperties, T> module, ModuleProperties properties) {
        this.id = id;
        this.module = module;
        this.properties = properties;

        properties.withId(id);
    }

    public ModuleContainer(SolsticeIdentifier id, Function<ModuleProperties, T> module) {
        this(id, module, new ModuleProperties());
    }

    public SolsticeIdentifier id() {
        return id;
    }

    public T get() {
        if (instance == null) instance = module.apply(properties);
        return instance;
    }

    public ModuleProperties properties() {
        return properties;
    }
}
