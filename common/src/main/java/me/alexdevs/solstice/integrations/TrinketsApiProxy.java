package me.alexdevs.solstice.integrations;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public abstract class TrinketsApiProxy {
    private static final TrinketsApiProxy INSTANCE = ServiceLoader.load(TrinketsApiProxy.class)
            .findFirst()
            .orElseThrow();

    public static TrinketsApiProxy get() {
        return INSTANCE;
    }

    // Basically, this does getTrinketComponent(target).getInventory().mapValues((map) -> map.mapValues(inv -> inv.values()))
    public abstract Optional<Map<String, Map<String, Container>>> getTrinketComponentInventory(LivingEntity target);
}
