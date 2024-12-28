package me.alexdevs.solstice.modules.feed;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.feed.commands.FeedCommand;

public class FeedModule extends ModuleBase {
    public static final String ID = "feed";

    public FeedModule() {
        super(ID);

        commands.add(new FeedCommand(this));
    }
}
