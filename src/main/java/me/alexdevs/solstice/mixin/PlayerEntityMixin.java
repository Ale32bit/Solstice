package me.alexdevs.solstice.mixin;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    private MutableText addTellClickEvent(MutableText component) {
        return null;
    }

    @Shadow
    public abstract Text getName();

    @Shadow
    public abstract GameProfile getGameProfile();

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<MutableText> cir) {
        var customNameModule = Solstice.modules.getModule(CustomNameModule.class);
        var name = customNameModule.getNameForPlayer((ServerPlayerEntity) (Object) this);
        cir.setReturnValue(addTellClickEvent(name));
    }

    @Inject(method = "findRespawnPosition", at = @At("RETURN"), cancellable = true)
    private static void solstice$overrideSpawn(ServerWorld world, BlockPos pos, float angle, boolean forced, boolean alive, CallbackInfoReturnable<Optional<Vec3d>> cir) {
        var spawnModule = Solstice.modules.getModule(SpawnModule.class);
        if (spawnModule.forceOnDeath()) {
            var spawn = spawnModule.getSpawn();
            cir.setReturnValue(Optional.of(new Vec3d(
                    spawn.x,
                    spawn.y,
                    spawn.z
            )));
        }
    }
}
