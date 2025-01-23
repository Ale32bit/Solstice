package me.alexdevs.solstice.modules.hat;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.hat.commands.HatCommand;
import me.alexdevs.solstice.modules.hat.data.HatConfig;
import me.alexdevs.solstice.modules.hat.data.HatLocale;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.stream.Stream;

public class HatModule extends ModuleBase.Toggleable {
    public static final String ID = "hat";

    public HatModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, HatConfig.class, HatConfig::new);
        Solstice.localeManager.registerModule(ID, HatLocale.MODULE);

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
            if(tags.contains(tag.id().toString()))
                return true;
        }
        return false;
    }
}
