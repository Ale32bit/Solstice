package me.alexdevs.solstice.mixin.modules.tablist;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class CustomPlayerListNameMixin {
    @Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
    private void solstice$customizePlayerListName(CallbackInfoReturnable<Component> callback) {
        if (Solstice.configManager.getData(TabListConfig.class).enable) {
            var player = (ServerPlayer) (Object) this;
            var playerContext = PlaceholderContext.of(player);
            var text = Format.parse(Solstice.configManager.getData(TabListConfig.class).playerTabName, playerContext);
            callback.setReturnValue(text);
        }
    }
}
