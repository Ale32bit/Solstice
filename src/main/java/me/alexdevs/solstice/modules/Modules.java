package me.alexdevs.solstice.modules;

import me.alexdevs.solstice.modules.admin.AdminModule;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.autoannouncement.AutoAnnouncementModule;
import me.alexdevs.solstice.modules.back.BackModule;
import me.alexdevs.solstice.modules.commandspy.CommandSpyModule;
import me.alexdevs.solstice.modules.core.CoreModule;
import me.alexdevs.solstice.modules.autorestart.AutoRestartModule;
import me.alexdevs.solstice.modules.formattablesigns.FormattableSignsModule;
import me.alexdevs.solstice.modules.home.HomeModule;
import me.alexdevs.solstice.modules.info.InfoModule;
import me.alexdevs.solstice.modules.mail.MailModule;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.modules.customname.CustomNameModule;
import me.alexdevs.solstice.modules.tablist.TabListModule;
import me.alexdevs.solstice.modules.timebar.TimeBarModule;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;

public class Modules {
    public final AdminModule admin = new AdminModule();
    public final AfkModule afk = new AfkModule();
    public final AutoAnnouncementModule autoAnnouncement = new AutoAnnouncementModule();
    public final AutoRestartModule autoRestart = new AutoRestartModule();
    public final BackModule back = new BackModule();
    public final CommandSpyModule commandSpy = new CommandSpyModule();
    public final CoreModule core = new CoreModule();
    public final FormattableSignsModule formattableSigns = new FormattableSignsModule();
    public final HomeModule home = new HomeModule();
    public final InfoModule info = new InfoModule();
    public final MailModule mail = new MailModule();
    public final ModerationModule moderation = new ModerationModule();
    public final CustomNameModule customName = new CustomNameModule();
    public final TabListModule tabList = new TabListModule();
    public final TimeBarModule timeBar = new TimeBarModule();
    public final UtilitiesModule utilities = new UtilitiesModule();
}
