package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

public class ModuleProperties {
    private SolsticeIdentifier id = null;
    private boolean enabled = true;
    private boolean toggleable = true;

    public ModuleProperties() {
    }

    public ModuleProperties withId(SolsticeIdentifier id) {
        this.id = id;
        return this;
    }

    public SolsticeIdentifier id() {
        return id;
    }

    public ModuleProperties enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isEnabled() {
        return !toggleable || enabled;
    }

    public ModuleProperties toggleable(boolean toggleable) {
        this.toggleable = toggleable;
        return this;
    }

    public boolean isToggleable() {
        return toggleable;
    }
}
