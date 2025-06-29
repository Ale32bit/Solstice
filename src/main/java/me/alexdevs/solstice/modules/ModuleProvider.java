package me.alexdevs.solstice.modules;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleEntrypoint;
import me.alexdevs.solstice.modules.admin.AdminModule;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.announcement.AnnouncementModule;
import me.alexdevs.solstice.modules.back.BackModule;
import me.alexdevs.solstice.modules.ban.BanModule;
import me.alexdevs.solstice.modules.broadcast.BroadcastModule;
import me.alexdevs.solstice.modules.commandSpy.CommandSpyModule;
import me.alexdevs.solstice.modules.cooldown.CooldownModule;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import me.alexdevs.solstice.modules.enderchest.EnderChestModule;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import me.alexdevs.solstice.modules.extinguish.ExtinguishModule;
import me.alexdevs.solstice.modules.feed.FeedModule;
import me.alexdevs.solstice.modules.fly.FlyModule;
import me.alexdevs.solstice.modules.god.GodModule;
import me.alexdevs.solstice.modules.hat.HatModule;
import me.alexdevs.solstice.modules.heal.HealModule;
import me.alexdevs.solstice.modules.helpOp.HelpOpModule;
import me.alexdevs.solstice.modules.home.HomeModule;
import me.alexdevs.solstice.modules.ignite.IgniteModule;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.info.InfoModule;
import me.alexdevs.solstice.modules.inventorySee.InventorySeeModule;
import me.alexdevs.solstice.modules.item.ItemModule;
import me.alexdevs.solstice.modules.jail.JailModule;
import me.alexdevs.solstice.modules.kick.KickModule;
import me.alexdevs.solstice.modules.kit.KitModule;
import me.alexdevs.solstice.modules.mail.MailModule;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import me.alexdevs.solstice.modules.mute.MuteModule;
import me.alexdevs.solstice.modules.near.NearModule;
import me.alexdevs.solstice.modules.note.NoteModule;
import me.alexdevs.solstice.modules.notifications.NotificationsModule;
import me.alexdevs.solstice.modules.placeholders.PlaceholdersModule;
import me.alexdevs.solstice.modules.powertool.PowerToolModule;
import me.alexdevs.solstice.modules.restart.RestartModule;
import me.alexdevs.solstice.modules.rtp.RTPModule;
import me.alexdevs.solstice.modules.seen.SeenModule;
import me.alexdevs.solstice.modules.sign.SignModule;
import me.alexdevs.solstice.modules.skull.SkullModule;
import me.alexdevs.solstice.modules.smite.SmiteModule;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.alexdevs.solstice.modules.staffChat.StaffChatModule;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.modules.sudo.SudoModule;
import me.alexdevs.solstice.modules.suicide.SuicideModule;
import me.alexdevs.solstice.modules.tablist.TabListModule;
import me.alexdevs.solstice.modules.teleportHere.TeleportHereModule;
import me.alexdevs.solstice.modules.teleportOffline.TeleportOfflineModule;
import me.alexdevs.solstice.modules.teleportPosition.TeleportPositionModule;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import me.alexdevs.solstice.modules.tell.TellModule;
import me.alexdevs.solstice.modules.timeBar.TimeBarModule;
import me.alexdevs.solstice.modules.trash.TrashModule;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;
import me.alexdevs.solstice.modules.warp.WarpModule;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class ModuleProvider implements ModuleEntrypoint {
    private static final Set<ModuleBase> MODULES = new HashSet<>();

    public static final AdminModule ADMIN = add(new AdminModule(path("admin")));
    public static final AfkModule AFK = add(new AfkModule(path("afk")));
    public static final AnnouncementModule ANNOUNCEMENT = add(new AnnouncementModule(path("autoannouncement")));
    public static final RestartModule RESTART = add(new RestartModule(path("restart")));
    public static final BackModule BACK = add(new BackModule(path("back")));
    public static final BanModule BAN = add(new BanModule(path("ban")));
    public static final BroadcastModule BROADCAST = add(new BroadcastModule(path("broadcast")));
    public static final CommandSpyModule COMMANDSPY = add(new CommandSpyModule(path("commandspy")));
    public static final CooldownModule COOLDOWN = add(new CooldownModule(path("cooldown")));
    public static final CustomNameModule CUSTOMNAME = add(new CustomNameModule(path("customname")));
    public static final EnderChestModule ENDERCHEST = add(new EnderChestModule(path("enderchest")));
    public static final ExperimentsModule EXPERIMENTS = add(new ExperimentsModule(path("experiments")));
    public static final ExtinguishModule EXTINGUISH = add(new ExtinguishModule(path("extinguish")));
    public static final FeedModule FEED = add(new FeedModule(path("feed")));
    public static final FlyModule FLY = add(new FlyModule(path("fly")));
    public static final SignModule SIGN = add(new SignModule(path("sign")));
    public static final GodModule GOD = add(new GodModule(path("god")));
    public static final HatModule HAT = add(new HatModule(path("hat")));
    public static final HealModule HEAL = add(new HealModule(path("heal")));
    public static final HelpOpModule HELPOP = add(new HelpOpModule(path("helpop")));
    public static final HomeModule HOME = add(new HomeModule(path("home")));
    public static final IgniteModule IGNITE = add(new IgniteModule(path("ignite")));
    public static final IgnoreModule IGNORE = add(new IgnoreModule(path("ignore")));
    public static final InfoModule INFO = add(new InfoModule(path("info")));
    public static final InventorySeeModule INVENTORYSEE = add(new InventorySeeModule(path("inventorysee")));
    public static final ItemModule ITEM = add(new ItemModule(path("item")));
    public static final JailModule JAIL = add(new JailModule(path("jail")));
    public static final KickModule KICK = add(new KickModule(path("kick")));
    public static final KitModule KIT = add(new KitModule(path("kit")));
    public static final MailModule MAIL = add(new MailModule(path("mail")));
    public static final MiscellaneousModule MISCELLANEOUS = add(new MiscellaneousModule(path("miscellaneous")));
    public static final MuteModule MUTE = add(new MuteModule(path("mute")));
    public static final NearModule NEAR = add(new NearModule(path("near")));
    public static final NoteModule NOTE = add(new NoteModule(path("note")));
    public static final NotificationsModule NOTIFICATIONS = add(new NotificationsModule(path("notifications")));
    public static final PlaceholdersModule PLACEHOLDERS = add(new PlaceholdersModule(path("placeholders")));
    public static final PowerToolModule POWERTOOL = add(new PowerToolModule(path("powertool")));
    public static final RTPModule RTP = add(new RTPModule(path("rtp")));
    public static final SeenModule SEEN = add(new SeenModule(path("seen")));
    public static final SkullModule SKULL = add(new SkullModule(path("skull")));
    public static final SmiteModule SMITE = add(new SmiteModule(path("smite")));
    public static final SpawnModule SPAWN = add(new SpawnModule(path("spawn")));
    public static final StaffChatModule STAFFCHAT = add(new StaffChatModule(path("staffchat")));
    public static final StylingModule STYLING = add(new StylingModule(path("styling")));
    public static final SudoModule SUDO = add(new SudoModule(path("sudo")));
    public static final SuicideModule SUICIDE = add(new SuicideModule(path("suicide")));
    public static final TabListModule TABLIST = add(new TabListModule(path("tablist")));
    public static final TeleportHereModule TELEPORTHERE = add(new TeleportHereModule(path("teleporthere")));
    public static final TeleportOfflineModule TELEPORTOFFLINE = add(new TeleportOfflineModule(path("teleportoffline")));
    public static final TeleportPositionModule TELEPORTPOSITION = add(new TeleportPositionModule(path("teleportposition")));
    public static final TeleportRequestModule TELEPORTREQUEST = add(new TeleportRequestModule(path("teleportrequest")));
    public static final TellModule TELL = add(new TellModule(path("tell")));
    public static final TimeBarModule TIMEBAR = add(new TimeBarModule(path("timebar")));
    public static final TrashModule TRASH = add(new TrashModule(path("trash")));
    public static final UtilitiesModule UTILITIES = add(new UtilitiesModule(path("utilities")));
    public static final WarpModule WARP = add(new WarpModule(path("warp")));

    private static ResourceLocation path(String path) {
        return Solstice.ID.withPath(path);
    }

    private static <T extends ModuleBase> T add(T module) {
        MODULES.add(module);
        return module;
    }

    @Override
    public HashSet<ModuleBase> register() {
        return new HashSet<>(MODULES);
    }
}
