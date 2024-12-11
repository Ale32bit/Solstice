package me.alexdevs.solstice.mixin;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.styling.formatters.DeathFormatter;
import me.alexdevs.solstice.api.ServerPosition;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import me.alexdevs.solstice.modules.back.BackModule;
import me.alexdevs.solstice.modules.tablist.data.TabListConfig;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    private static final NodeParser parser = NodeParser.merge(TextParserV1.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_PARSER);

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void solstice$customizePlayerListName(CallbackInfoReturnable<Text> callback) {
        if (Solstice.configManager.getData(TabListConfig.class).enable) {
            var player = (ServerPlayerEntity) (Object) this;
            var playerContext = PlaceholderContext.of(player);
            var text = Placeholders.parseText(parser.parseNode(Solstice.configManager.getData(TabListConfig.class).playerTabName), playerContext);
            callback.setReturnValue(text);
        }
    }

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"))
    private Text solstice$getDeathMessage(DamageTracker instance) {
        var player = (ServerPlayerEntity) (Object) this;
        return DeathFormatter.onDeath(player, instance);
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at = @At("HEAD"))
    public void solstice$requestTeleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, CallbackInfoReturnable<Boolean> cir) {
        var player = (ServerPlayerEntity) (Object) this;
        BackModule.lastPlayerPositions.put(player.getUuid(), new ServerPosition(player));
    }
}
