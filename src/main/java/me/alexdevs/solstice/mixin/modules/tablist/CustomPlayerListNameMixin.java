package me.alexdevs.solstice.mixin.modules.tablist;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class CustomPlayerListNameMixin {
    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void solstice$customizePlayerListName(CallbackInfoReturnable<Text> callback) {
        if (Solstice.configManager.getData(TabListConfig.class).enable) {
            var player = (ServerPlayerEntity) (Object) this;
            var playerContext = PlaceholderContext.of(player);
            var text = Format.parse(Solstice.configManager.getData(TabListConfig.class).playerTabName, playerContext);
            callback.setReturnValue(text);
        }
    }
}
