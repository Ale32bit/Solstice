package me.alexdevs.solstice.modules.near;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.near.commands.NearCommand;
import me.alexdevs.solstice.modules.near.data.NearConfig;
import me.alexdevs.solstice.modules.near.data.NearLocale;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
public class NearModule extends ModuleBase.Toggleable {
    

    public NearModule(SolsticeIdentifier id) {
        super(id);
    }

    @Override
    public void init() {
        registerConfig(NearConfig.class, NearConfig::new);
        registerLocale(NearLocale.MODULE);

        commands.add(new NearCommand(this));
    }
}
