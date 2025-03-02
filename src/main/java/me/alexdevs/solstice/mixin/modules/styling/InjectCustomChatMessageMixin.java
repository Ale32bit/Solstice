package me.alexdevs.solstice.mixin.modules.styling;

import com.mojang.datafixers.util.Pair;
import me.alexdevs.solstice.modules.styling.StylingModule;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ChatTypeDecoration;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class InjectCustomChatMessageMixin {

    @SuppressWarnings("unchecked")
    @Inject(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void solstice$load(ResourceManager resourceManager, RegistryAccess baseRegistryManager, List<RegistryDataLoader.RegistryData<?>> entries,
                                      CallbackInfoReturnable<RegistryAccess.Frozen> cir, Map _unused, List<Pair<WritableRegistry<?>, Object>> list) {
        for (var pair : list) {
            var registry = pair.getFirst();
            if (registry.key().equals(Registries.CHAT_TYPE)) {
                Registry.register((Registry<ChatType>) registry, StylingModule.CHAT_TYPE,
                        new ChatType(
                                ChatTypeDecoration.withSender("%s"),
                                ChatTypeDecoration.withSender("%s")
                        ));
            }
        }
    }

}
