package me.alexdevs.solstice.api.module;

import org.jetbrains.annotations.NotNull;

public class ModuleContainer implements Comparable<ModuleContainer> {
    private final String id;
    private final String name;
    private final boolean isRequired;
    private final IModule module;

    public static ModuleContainer of(String id, String name, IModule module) {
        return new ModuleContainer(id, name, false, module);
    }

    public ModuleContainer(String id, String name, boolean isRequired, IModule module) {
        this.id = id;
        this.name = name;
        this.isRequired = isRequired;
        this.module = module;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isRequired() {
        return isRequired;
    }
    public IModule getModule() {
        return module;
    }

    @Override
    public int compareTo(@NotNull final ModuleContainer o) {
        return this.id.compareTo(o.id);
    }
}
