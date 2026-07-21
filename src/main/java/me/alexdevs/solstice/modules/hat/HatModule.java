package me.alexdevs.solstice.modules.hat;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.modules.hat.commands.HatCommand;
import me.alexdevs.solstice.modules.hat.data.HatConfig;
import me.alexdevs.solstice.modules.hat.data.HatLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.stream.Stream;
public class HatModule extends ModuleBase {
    

    public HatModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerConfig(HatConfig.class, HatConfig::new);
        registerLocale(HatLocale.MODULE);

        commands.add(new HatCommand(this));
    }

    public HatConfig getConfig() {
        return Solstice.configManager.getData(HatConfig.class);
    }

    public List<String> getConfigTags() {
        return getConfig().filter.stream().filter(s -> s.startsWith("#")).toList();
    }

    public List<String> getConfigItems() {
        return getConfig().filter.stream().filter(s -> !s.startsWith("#")).toList();
    }

    public boolean isInFilter(String key) {
        if (key.startsWith("#")) {
            return getConfigTags().contains(key);
        } else {
            return getConfigItems().contains(key);
        }
    }

    public boolean isInFilter(Stream<TagKey<Item>> stream) {
        var tags = getConfigTags().stream().map(t -> t.substring(1)).toList();
        var iter = stream.iterator();
        while (iter.hasNext()) {
            var tag = iter.next();
            if(tags.contains(tag.location().toString()))
                return true;
        }
        return false;
    }
}
