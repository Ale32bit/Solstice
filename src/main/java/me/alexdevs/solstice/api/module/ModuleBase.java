package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.Solstice;

import java.util.Collection;

public abstract class ModuleBase {
    protected final String id;

    public ModuleBase(String id) {
        this.id = id;
    }

    public abstract Collection<? extends ModCommand<?>> getCommands();

    public String getId() {
        return id;
    }

    public String getPermissionNode() {
        return Solstice.MOD_ID + "." + id;
    }

    public String getPermissionNode(String sub) {
        return getPermissionNode() + "." + sub;
    }
}
