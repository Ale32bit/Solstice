package me.alexdevs.solstice.modules.feed;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.feed.commands.FeedCommand;
import me.alexdevs.solstice.modules.feed.data.FeedLocale;
import net.minecraft.resources.ResourceLocation;
public class FeedModule extends ModuleBase.Toggleable {
    

    public FeedModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(FeedLocale.MODULE);

        commands.add(new FeedCommand(this));
    }
}
