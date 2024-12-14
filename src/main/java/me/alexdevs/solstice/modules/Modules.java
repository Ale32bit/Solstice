package me.alexdevs.solstice.modules;

import me.alexdevs.solstice.modules.admin.AdminModule;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.autoannouncement.AutoAnnouncementModule;
import me.alexdevs.solstice.modules.back.BackModule;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import me.alexdevs.solstice.modules.fun.FunModule;
import me.alexdevs.solstice.modules.helpop.HelpOpModule;
import me.alexdevs.solstice.modules.near.NearModule;
import me.alexdevs.solstice.modules.seen.SeenModule;
import me.alexdevs.solstice.modules.staffchat.StaffChatModule;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.modules.commandspy.CommandSpyModule;
import me.alexdevs.solstice.modules.core.CoreModule;
import me.alexdevs.solstice.modules.autorestart.AutoRestartModule;
import me.alexdevs.solstice.modules.formattablesigns.FormattableSignsModule;
import me.alexdevs.solstice.modules.home.HomeModule;
import me.alexdevs.solstice.modules.info.InfoModule;
import me.alexdevs.solstice.modules.mail.MailModule;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.modules.customname.CustomNameModule;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.alexdevs.solstice.modules.tablist.TabListModule;
import me.alexdevs.solstice.modules.teleport.TeleportModule;
import me.alexdevs.solstice.modules.tell.TellModule;
import me.alexdevs.solstice.modules.timebar.TimeBarModule;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;
import me.alexdevs.solstice.modules.warp.WarpModule;

public class Modules {
    public final AdminModule admin = new AdminModule();
    public final AfkModule afk = new AfkModule();
    public final AutoAnnouncementModule autoAnnouncement = new AutoAnnouncementModule();
    public final AutoRestartModule autoRestart = new AutoRestartModule();
    public final BackModule back = new BackModule();
    public final CommandSpyModule commandSpy = new CommandSpyModule();
    public final CoreModule core = new CoreModule();
    public final CustomNameModule customName = new CustomNameModule();
    public final FormattableSignsModule formattableSigns = new FormattableSignsModule();
    public final FunModule fun = new FunModule();
    public final HelpOpModule helpOp = new HelpOpModule();
    public final HomeModule home = new HomeModule();
    public final InfoModule info = new InfoModule();
    public final MailModule mail = new MailModule();
    public final ModerationModule moderation = new ModerationModule();
    public final NearModule near = new NearModule();
    public final SeenModule seen = new SeenModule();
    public final SpawnModule spawn = new SpawnModule();
    public final StaffChatModule staffChat = new StaffChatModule();
    public final StylingModule styling = new StylingModule();
    public final TabListModule tabList = new TabListModule();
    public final TeleportModule teleport = new TeleportModule();
    public final TellModule tell = new TellModule();
    public final TimeBarModule timeBar = new TimeBarModule();
    public final UtilitiesModule utilities = new UtilitiesModule();
    public final WarpModule warp = new WarpModule();

    public final ExperimentsModule experiments = new ExperimentsModule();
}
