package me.alexdevs.solstice.modules.kit;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.kit.commands.KitCommand;
import me.alexdevs.solstice.modules.kit.data.Kit;
import me.alexdevs.solstice.modules.kit.data.KitPlayerData;
import me.alexdevs.solstice.modules.kit.data.KitServerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitModule extends ModuleBase {
    public static final String ID = "kit";

    public static RegistryWrapper.WrapperLookup wrapperLookup;

    public KitModule() {
        super(ID);

        Solstice.playerData.registerData(ID, KitPlayerData.class, KitPlayerData::new);
        Solstice.serverData.registerData(ID, KitServerData.class, KitServerData::new);

        //commands.add(new KitCommand(this)); // TODO: work in progress
    }

    public Map<String, Kit> getKits() {
        return Solstice.serverData.getData(KitServerData.class).kits;
    }
}
