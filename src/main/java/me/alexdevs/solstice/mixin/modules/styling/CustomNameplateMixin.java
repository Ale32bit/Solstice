package me.alexdevs.solstice.mixin.modules.styling;

import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class CustomNameplateMixin {

    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(method = "updateEntireScoreboard", at = @At("HEAD"))
    private void solstice$overrideNameplate(ServerScoreboard scoreboard, ServerPlayer player, CallbackInfo ci) {
        ModuleProvider.STYLING.sendTeamSetup(player, players, scoreboard, true);
    }

}
