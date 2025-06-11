package me.alexdevs.solstice.fabric;

import dev.emi.trinkets.api.TrinketsApi;
import me.alexdevs.solstice.integrations.TrinketsApiProxy;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.Optional;

public class FabricTrinketsApi extends TrinketsApiProxy {
    // Basically, this does getTrinketComponent(target).getInventory().mapValues((map) -> map.mapValues(inv -> inv.values()))
    @SuppressWarnings("unchecked")
    @Override
    public Optional<Map<String, Map<String, Container>>> getTrinketComponentInventory(LivingEntity target) {
        return TrinketsApi.getTrinketComponent(target)
                .map(comp -> Map.ofEntries(comp.getInventory().entrySet().stream().map(entry -> Map.entry(
                        entry.getKey(),
                        Map.ofEntries(entry.getValue()
                                .entrySet()
                                .stream()
                                .map(it -> Map.entry(it.getKey(), (Container) it.getValue()))
                                .toArray(Map.Entry[]::new))
                )).toArray(Map.Entry[]::new)));
    }
}
