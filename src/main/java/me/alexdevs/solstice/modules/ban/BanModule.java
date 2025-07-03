package me.alexdevs.solstice.modules.ban;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.ban.commands.BanCommand;
import me.alexdevs.solstice.modules.ban.commands.TempBanCommand;
import me.alexdevs.solstice.modules.ban.commands.UnbanCommand;
import me.alexdevs.solstice.modules.ban.data.BanLocale;
import net.minecraft.resources.ResourceLocation;

public class BanModule extends ModuleBase.Toggleable {
    public BanModule(ResourceLocation id) {
        super(id);
    }

    @Override
    public void init() {
        registerLocale(BanLocale.MODULE);

        commands.add(new BanCommand(this));
        commands.add(new TempBanCommand(this));
        commands.add(new UnbanCommand(this));
    }
}
