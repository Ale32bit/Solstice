package me.alexdevs.solstice.mixin.modules.styling;

import com.mojang.datafixers.util.Pair;
import me.alexdevs.solstice.modules.styling.StylingModule;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class InjectCustomChatMessageMixin {

    /*
    @SuppressWarnings("unchecked")
    @Inject(method = "load(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void solstice$load(ResourceManager resourceManager, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries,
                                      CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir, Map _unused, List<Pair<MutableRegistry<?>, Object>> list) {
        for (var pair : list) {
            var registry = pair.getFirst();
            if (registry.getKey().equals(RegistryKeys.MESSAGE_TYPE)) {
                Registry.register((Registry<MessageType>) registry, StylingModule.CHAT_TYPE,
                        new MessageType(
                                Decoration.ofChat("%s"),
                                Decoration.ofChat("%s")
                        ));
            }
        }
    }*/

    /*@SuppressWarnings("unchecked")
    @Inject(method = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/resources/RegistryDataLoader$LoadingFunction;Lnet/minecraft/core/RegistryAccess;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void solstice$load(RegistryDataLoader.LoadingFunction loadingFunction, RegistryAccess registryAccess, List<RegistryDataLoader.RegistryData<?>> registryData, CallbackInfoReturnable<RegistryAccess.Frozen> cir, Map map, List list, RegistryOps.RegistryInfoLookup registryInfoLookup) {
        for (var entry : entries) {
            var registry = entry.key();
            if (registry.getRegistry().equals(RegistryKeys.MESSAGE_TYPE)) {
                Registry.register((Registry<MessageType>) registry, StylingModule.CHAT_TYPE,
                        new MessageType(
                                Decoration.ofChat("%s"),
                                Decoration.ofChat("%s")
                        ));
            }
        }
    }*/
}
