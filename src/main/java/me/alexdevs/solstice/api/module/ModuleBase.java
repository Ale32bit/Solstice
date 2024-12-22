package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.locale.Locale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class ModuleBase implements Comparable<ModuleBase> {
    protected final String id;
    protected final List<ModCommand<?>> commands = new ArrayList<>();

    public ModuleBase(String id) {
        this.id = id;
    }

    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }

    public String getId() {
        return id;
    }

    public String getPermissionNode() {
        return Solstice.MOD_ID + "." + id.toLowerCase();
    }

    public String getPermissionNode(String sub) {
        return getPermissionNode() + "." + sub;
    }

    public Locale locale() {
        return Solstice.localeManager.getLocale(id);
    }

    @Override
    public String toString() {
        return "Module [" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleBase that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(ModuleBase o) {
        return id.compareTo(o.id);
    }
}
