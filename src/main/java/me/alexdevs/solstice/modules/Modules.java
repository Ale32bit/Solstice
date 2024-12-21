package me.alexdevs.solstice.modules;

import com.mojang.brigadier.CommandDispatcher;
import me.alexdevs.solstice.api.events.ModuleEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.modules.admin.AdminModule;
import me.alexdevs.solstice.modules.afk.AfkModule;
import me.alexdevs.solstice.modules.autoAnnouncement.AutoAnnouncementModule;
import me.alexdevs.solstice.modules.back.BackModule;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import me.alexdevs.solstice.modules.hat.HatModule;
import me.alexdevs.solstice.modules.helpOp.HelpOpModule;
import me.alexdevs.solstice.modules.near.NearModule;
import me.alexdevs.solstice.modules.seen.SeenModule;
import me.alexdevs.solstice.modules.staffChat.StaffChatModule;
import me.alexdevs.solstice.modules.styling.StylingModule;
import me.alexdevs.solstice.modules.commandSpy.CommandSpyModule;
import me.alexdevs.solstice.modules.core.CoreModule;
import me.alexdevs.solstice.modules.autoRestart.AutoRestartModule;
import me.alexdevs.solstice.modules.formattableSigns.FormattableSignsModule;
import me.alexdevs.solstice.modules.home.HomeModule;
import me.alexdevs.solstice.modules.info.InfoModule;
import me.alexdevs.solstice.modules.mail.MailModule;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import me.alexdevs.solstice.modules.spawn.SpawnModule;
import me.alexdevs.solstice.modules.sudo.SudoModule;
import me.alexdevs.solstice.modules.tablist.TabListModule;
import me.alexdevs.solstice.modules.teleportRequest.TeleportRequestModule;
import me.alexdevs.solstice.modules.tell.TellModule;
import me.alexdevs.solstice.modules.timeBar.TimeBarModule;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;
import me.alexdevs.solstice.modules.warp.WarpModule;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class Modules {
    public final List<? extends ModuleBase> modules = List.of(
            new AdminModule(),
            new AfkModule(),
            new AutoAnnouncementModule(),
            new AutoRestartModule(),
            new BackModule(),
            new CommandSpyModule(),
            new CoreModule(),
            new CustomNameModule(),
            new FormattableSignsModule(),
            new HatModule(),
            new HelpOpModule(),
            new HomeModule(),
            new InfoModule(),
            new MailModule(),
            new ModerationModule(),
            new NearModule(),
            new SeenModule(),
            new SpawnModule(),
            new StaffChatModule(),
            new StylingModule(),
            new SudoModule(),
            new TabListModule(),
            new TeleportRequestModule(),
            new TellModule(),
            new TimeBarModule(),
            new UtilitiesModule(),
            new WarpModule(),
            new ExperimentsModule()
    );

    public Modules() {
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    public <T> T getModule(Class<T> classOfModule) {
        for (var module : modules) {
            if (classOfModule.isInstance(module)) {
                return classOfModule.cast(module);
            }
        }
        return null;
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, CommandManager.RegistrationEnvironment environment) {
        var commands = ModuleEvents.COMMAND.invoker().register(dispatcher, commandRegistry, environment);
    }
}
