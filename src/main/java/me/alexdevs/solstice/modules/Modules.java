package me.alexdevs.solstice.modules;

import me.alexdevs.solstice.modules.admin.AdminModule;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.core.CoreModule;
import me.alexdevs.solstice.modules.autorestart.AutoRestartModule;
import me.alexdevs.solstice.modules.home.HomeModule;
import me.alexdevs.solstice.modules.info.InfoModule;
import me.alexdevs.solstice.modules.timebar.TimeBarModule;

public class Modules {
    public final CoreModule core;
    public final AutoRestartModule autoRestart;
    public final TimeBarModule timeBar;
    public final AdminModule admin;
    public final HomeModule home;
    public final AfkModule afk;
    public final InfoModule info;

    public Modules() {
        core = new CoreModule();
        autoRestart = new AutoRestartModule();
        timeBar = new TimeBarModule();
        admin = new AdminModule();
        home = new HomeModule();
        afk = new AfkModule();
        info = new InfoModule();
    }
}
