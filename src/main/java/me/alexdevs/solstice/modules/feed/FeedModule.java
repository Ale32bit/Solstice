package me.alexdevs.solstice.modules.feed;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.feed.commands.FeedCommand;
import me.alexdevs.solstice.modules.feed.data.FeedLocale;

public class FeedModule extends ModuleBase.Toggleable {
    public static final String ID = "feed";

    public FeedModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.localeManager.registerModule(ID, FeedLocale.MODULE);

        commands.add(new FeedCommand(this));
    }
}
