package me.alexdevs.solstice.neoforge;

import me.alexdevs.solstice.integrations.TrinketsApiProxy;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.Optional;

public class EmptyTrinketsApi extends TrinketsApiProxy {
    @Override
    public Optional<Map<String, Map<String, Container>>> getTrinketComponentInventory(LivingEntity target) {
        throw new UnsupportedOperationException("Cannot use Trinkets on Forge!");
    }
}
