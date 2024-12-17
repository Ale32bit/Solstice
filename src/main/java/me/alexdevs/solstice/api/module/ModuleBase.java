package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.Solstice;

public abstract class ModuleBase {
    public final String id;
    public ModuleBase(String id) {
        this.id = id;
    }

    public String getPermissionNode() {
        return Solstice.MOD_ID + "." + id;
    }

    public String getPermissionNode(String sub) {
        return getPermissionNode() + "." + sub;
    }
}
