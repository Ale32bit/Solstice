package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.core.ToggleableConfig;
import me.alexdevs.solstice.locale.Locale;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Supplier;

public abstract class ModuleBase implements Comparable<ModuleBase> {
    protected final ResourceLocation id;
    protected final List<ModCommand<?>> commands = new ArrayList<>();
    protected Class<?> configClass = null;
    protected Class<?> playerDataClass = null;
    protected Class<?> serverDataClass = null;

    public ModuleBase(ResourceLocation id) {
        this.id = id;
    }

    /**
     * This method is called when Solstice is ready to initialize modules.
     * <p>
     * If the module is a {@linkplain Toggleable}, it will check if it is enabled before calling it.
     */
    public abstract void init();

    public Collection<? extends ModCommand<?>> getCommands() {
        return commands;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getPermissionNode() {
        return id.getNamespace() + "." + id.getPath();
    }

    public String getPermissionNode(String sub) {
        return getPermissionNode() + "." + sub;
    }

    public Locale locale() {
        return Solstice.localeManager.getLocale(id);
    }

    public <T> void registerConfig(Class<T> clazz, Supplier<T> creator) {
        Solstice.configManager.registerData(this.id, clazz, creator);
    }

    public <T> void registerPlayerData(Class<T> clazz, Supplier<T> creator) {
        Solstice.playerData.registerData(this.id, clazz, creator);
    }

    public <T> void registerServerData(Class<T> clazz, Supplier<T> creator) {
        Solstice.serverData.registerData(this.id, clazz, creator);
    }

    public void registerLocale(Map<String, String> map) {
        Solstice.localeManager.registerModule(this.id, map);
    }

    public void registerSharedLocale(Map<String, String> map) {
        Solstice.localeManager.registerShared(map);
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

    public static abstract class Toggleable extends ModuleBase {
        public Toggleable(ResourceLocation id) {
            super(id);
        }

        public boolean isEnabled() {
            return ToggleableConfig.get().isEnabled(this.id.toString());
        }
    }
}
