package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.locale.Locale;

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

    public Locale locale() {
        return Solstice.localeManager.getLocale(id);
    }
}
