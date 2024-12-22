package me.alexdevs.solstice.modules.ban;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ban.commands.BanCommand;
import me.alexdevs.solstice.modules.ban.commands.TempBanCommand;
import me.alexdevs.solstice.modules.ban.commands.UnbanCommand;
import me.alexdevs.solstice.modules.ban.data.BanLocale;

public class BanModule extends ModuleBase {
    public static final String ID = "ban";

    public BanModule() {
        super(ID);

        Solstice.localeManager.registerModule(ID, BanLocale.MODULE);

        commands.add(new BanCommand(this));
        commands.add(new TempBanCommand(this));
        commands.add(new UnbanCommand(this));
    }
}
