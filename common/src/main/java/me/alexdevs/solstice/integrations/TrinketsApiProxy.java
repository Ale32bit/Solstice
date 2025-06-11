package me.alexdevs.solstice.integrations;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;

public abstract class TrinketsApiProxy {
    private static TrinketsApiProxy INSTANCE;

    public static TrinketsApiProxy get() {
        return INSTANCE;
    }

    @ApiStatus.Internal
    public static void set(TrinketsApiProxy proxy) {
        INSTANCE = proxy;
    }

    // Basically, this does getTrinketComponent(target).getInventory().mapValues((map) -> map.mapValues(inv -> inv.values()))
    public abstract Optional<Map<String, Map<String, Container>>> getTrinketComponentInventory(LivingEntity target);
}
