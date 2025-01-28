package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.core.ToggleableConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SolsticeMixinConfigPlugin implements IMixinConfigPlugin {
    private final ToggleableConfig config = ToggleableConfig.get();
    public static final String packageBase = "me.alexdevs.solstice.mixin.modules.";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(packageBase)) {
            var moduleMixin = mixinClassName.replace(packageBase, "");
            var parts = moduleMixin.split("\\.");
            var module = parts[0].toLowerCase();
            return config.isEnabled(module);
        }
        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
