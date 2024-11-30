package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.Solstice;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Decoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {

    @SuppressWarnings("unchecked")
    @Inject(method = "load(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void solstice$load(ResourceManager resourceManager, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries,
                                       CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir, Map _unused, List<Pair<MutableRegistry<?>, Object>> list) {
        for (var pair : list) {
            var registry = pair.getFirst();
            if(registry.getKey().equals(RegistryKeys.MESSAGE_TYPE)) {
                Registry.register((Registry<MessageType>) registry, Solstice.CHAT_TYPE,
                        new MessageType(
                                Decoration.ofChat("%s"),
                                Decoration.ofChat("%s")
                        ));
            }
        }
    }

}
