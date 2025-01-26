package me.alexdevs.solstice.modules;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleEntrypoint;
import me.alexdevs.solstice.modules.admin.AdminModule;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.autoAnnouncement.AutoAnnouncementModule;
import me.alexdevs.solstice.modules.item.ItemModule;
import me.alexdevs.solstice.modules.jail.JailModule;
import me.alexdevs.solstice.modules.kit.KitModule;
import me.alexdevs.solstice.modules.miscellaneous.MiscellaneousModule;
import me.alexdevs.solstice.modules.note.NoteModule;
import me.alexdevs.solstice.modules.notifications.NotificationsModule;
import me.alexdevs.solstice.modules.placeholders.PlaceholdersModule;
import me.alexdevs.solstice.modules.powertool.PowerToolModule;
import me.alexdevs.solstice.modules.restart.RestartModule;
import me.alexdevs.solstice.modules.back.BackModule;
import me.alexdevs.solstice.modules.ban.BanModule;
import me.alexdevs.solstice.modules.broadcast.BroadcastModule;
import me.alexdevs.solstice.modules.commandSpy.CommandSpyModule;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import me.alexdevs.solstice.modules.enderchest.EnderChestModule;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import me.alexdevs.solstice.modules.extinguish.ExtinguishModule;
import me.alexdevs.solstice.modules.feed.FeedModule;
import me.alexdevs.solstice.modules.fly.FlyModule;
import me.alexdevs.solstice.modules.rtp.RTPModule;
import me.alexdevs.solstice.modules.sign.SignModule;
import me.alexdevs.solstice.modules.god.GodModule;
import me.alexdevs.solstice.modules.hat.HatModule;
import me.alexdevs.solstice.modules.heal.HealModule;
import me.alexdevs.solstice.modules.helpOp.HelpOpModule;
import me.alexdevs.solstice.modules.home.HomeModule;
import me.alexdevs.solstice.modules.ignite.IgniteModule;
import me.alexdevs.solstice.modules.ignore.IgnoreModule;
import me.alexdevs.solstice.modules.info.InfoModule;
import me.alexdevs.solstice.modules.inventorySee.InventorySeeModule;
import me.alexdevs.solstice.modules.kick.KickModule;
import me.alexdevs.solstice.modules.mail.MailModule;
import me.alexdevs.solstice.modules.mute.MuteModule;
import me.alexdevs.solstice.modules.near.NearModule;
import me.alexdevs.solstice.modules.seen.SeenModule;
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
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import me.alexdevs.solstice.modules.tell.TellModule;
import me.alexdevs.solstice.modules.timeBar.TimeBarModule;
import me.alexdevs.solstice.modules.trash.TrashModule;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;
import me.alexdevs.solstice.modules.warp.WarpModule;

import java.util.HashSet;
import java.util.List;

public class ModuleProvider implements ModuleEntrypoint {
    private static final List<? extends ModuleBase> modules = List.of(
            new AdminModule(),
            new AfkModule(),
            new AutoAnnouncementModule(),
            new RestartModule(),
            new BackModule(),
            new BanModule(),
            new BroadcastModule(),
            new CommandSpyModule(),
            new CustomNameModule(),
            new EnderChestModule(),
            new ExperimentsModule(),
            new ExtinguishModule(),
            new FeedModule(),
            new FlyModule(),
            new SignModule(),
            new GodModule(),
            new HatModule(),
            new HealModule(),
            new HelpOpModule(),
            new HomeModule(),
            new IgniteModule(),
            new IgnoreModule(),
            new InfoModule(),
            new InventorySeeModule(),
            new ItemModule(),
            new JailModule(),
            new KickModule(),
            new KitModule(),
            new MailModule(),
            new MiscellaneousModule(),
            new MuteModule(),
            new NearModule(),
            new NoteModule(),
            new NotificationsModule(),
            new PlaceholdersModule(),
            new PowerToolModule(),
            new RTPModule(),
            new SeenModule(),
            new SkullModule(),
            new SmiteModule(),
            new SpawnModule(),
            new StaffChatModule(),
            new StylingModule(),
            new SudoModule(),
            new SuicideModule(),
            new TabListModule(),
            new TeleportHereModule(),
            new TeleportOfflineModule(),
            new TeleportRequestModule(),
            new TellModule(),
            new TimeBarModule(),
            new TrashModule(),
            new UtilitiesModule(),
            new WarpModule()
    );

    @Override
    public HashSet<ModuleBase> register() {
        return new HashSet<>(modules);
    }
}
