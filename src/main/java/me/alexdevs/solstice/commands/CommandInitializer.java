package me.alexdevs.solstice.commands;

import me.alexdevs.solstice.commands.admin.*;
import me.alexdevs.solstice.commands.misc.*;
import me.alexdevs.solstice.commands.teleport.*;
import me.alexdevs.solstice.commands.home.DeleteHomeCommand;
import me.alexdevs.solstice.commands.home.HomeCommand;
import me.alexdevs.solstice.commands.home.HomesCommand;
import me.alexdevs.solstice.commands.home.SetHomeCommand;
import me.alexdevs.solstice.commands.spawn.DelSpawnCommand;
import me.alexdevs.solstice.commands.spawn.SetSpawnCommand;
import me.alexdevs.solstice.commands.spawn.SpawnCommand;
import me.alexdevs.solstice.commands.tell.ReplyCommand;
import me.alexdevs.solstice.commands.tell.TellCommand;
import me.alexdevs.solstice.commands.warp.DeleteWarpCommand;
import me.alexdevs.solstice.commands.warp.SetWarpCommand;
import me.alexdevs.solstice.commands.warp.WarpCommand;
import me.alexdevs.solstice.commands.warp.WarpsCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandInitializer {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SolsticeCommand.register(dispatcher);

            AfkCommand.register(dispatcher);

            TellCommand.register(dispatcher);
            ReplyCommand.register(dispatcher);

            TeleportAskCommand.register(dispatcher);
            TeleportAskHereCommand.register(dispatcher);
            TeleportAcceptCommand.register(dispatcher);
            TeleportDenyCommand.register(dispatcher);
            BackCommand.register(dispatcher);
            TeleportOfflineCommand.register(dispatcher);

            FlyCommand.register(dispatcher);
            GodCommand.register(dispatcher);
            SudoCommand.register(dispatcher);
            BroadcastCommand.register(dispatcher);

            SetSpawnCommand.register(dispatcher);
            DelSpawnCommand.register(dispatcher);
            SpawnCommand.register(dispatcher);

            HomeCommand.register(dispatcher);
            SetHomeCommand.register(dispatcher);
            DeleteHomeCommand.register(dispatcher);
            HomesCommand.register(dispatcher);

            WarpCommand.register(dispatcher);
            SetWarpCommand.register(dispatcher);
            DeleteWarpCommand.register(dispatcher);
            WarpsCommand.register(dispatcher);

            TimeBarCommand.register(dispatcher);
            RestartCommand.register(dispatcher);
            MuteCommand.register(dispatcher);

            NearCommand.register(dispatcher);
            MailCommand.register(dispatcher);
            SeenCommand.register(dispatcher);
            MotdCommand.register(dispatcher);
            SuicideCommand.register(dispatcher);
            SmiteCommand.register(dispatcher);
        });
    }
}