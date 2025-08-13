package me.alexdevs.solstice.mixin.modules.tablist;

import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Mixin(ClientboundPlayerInfoUpdatePacket.class)
public abstract class PlayerOrderMixin {
    /*// Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;entries:Ljava/util/List;
    @Redirect(method = "<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"))
    public List<ClientboundPlayerInfoUpdatePacket.Entry> solstice$overridePlayerOrder(Stream<ClientboundPlayerInfoUpdatePacket.Entry> stream) {
        var groups = ModuleProvider.TABLIST.getGroupsOrder();
        var groupOrder = new HashMap<String, Integer>();
        for (int i = 0; i < groups.size(); i++) {
            groupOrder.put(groups.get(i), i);
        }

        var list = stream
                .sorted(Comparator.comparingInt(entry -> {
                    var player = ModuleProvider.TABLIST.getPlayerByProfile(entry.profile());
                    // Get the player's group index, default to large number if no group matches
                    return groupOrder.getOrDefault(
                            LuckPermsIntegration.getPrimaryGroup(player),
                            Integer.MAX_VALUE
                    );
                }))
                .toList();

        return list;
    }*/
}
