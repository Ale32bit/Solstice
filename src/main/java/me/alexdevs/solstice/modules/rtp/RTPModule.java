package me.alexdevs.solstice.modules.rtp;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.rtp.commands.RTPCommand;
import me.alexdevs.solstice.modules.rtp.core.Locator;
import me.alexdevs.solstice.modules.rtp.data.RTPConfig;
import me.alexdevs.solstice.modules.rtp.data.RTPLocale;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;

public class RTPModule extends ModuleBase {
    public static final String ID = "rtp";

    private final ArrayList<Locator> locators = new ArrayList<>();

    public RTPModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, RTPLocale.MODULE);
        Solstice.configManager.registerData(ID, RTPConfig.class, RTPConfig::new);

        commands.add(new RTPCommand(this));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            locators.removeIf(Locator::tick);
        });
    }

    public RTPConfig getConfig() {
        return Solstice.configManager.getData(RTPConfig.class);
    }

    public Locator createLocator(ServerPlayerEntity player) {
        var locator = new Locator(player.getServerWorld(), getConfig());
        locators.add(locator);
        return locator;
    }
}
