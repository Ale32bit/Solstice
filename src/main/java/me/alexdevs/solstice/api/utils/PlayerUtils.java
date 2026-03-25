package me.alexdevs.solstice.api.utils;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import net.minecraft.server.MinecraftServer;
//? if >= 1.21.1 {
import net.minecraft.server.level.ClientInformation;
//? }
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;
import java.util.UUID;

public class PlayerUtils {
    public static boolean isOnline(UUID uuid) {
        return Solstice.server.getPlayerList().getPlayer(uuid) != null;
    }

    public static ServerPlayer loadOfflinePlayer(GameProfile profile) {
        if (isOnline(PlayerUtils.getId(profile))) {
            return null;
        }

        var playerManager = Solstice.server.getPlayerList();

        //? if >= 1.21.11 {
        /*var player =  new ServerPlayer(Solstice.server, Solstice.server.overworld(), profile, ClientInformation.createDefault());

        var nameAndId = new net.minecraft.server.players.NameAndId(profile.id(),profile.name());
        playerManager.loadPlayerData(nameAndId).ifPresent(tag -> {
            var input = net.minecraft.world.level.storage.TagValueInput.create(
                    net.minecraft.util.ProblemReporter.DISCARDING,
                    Solstice.server.registryAccess(),
                    tag
            );
            player.load(input);
        });
        *///? } else if >= 1.21.1 {
        var player = playerManager.getPlayerForLogin(profile, ClientInformation.createDefault());
        //? } else {
        /*var player = playerManager.getPlayerForLogin(profile);
        *///? }


        //? if >= 1.21.11
        //Solstice.server.playerDataStorage.load(new net.minecraft.server.players.NameAndId(profile.id(), profile.name()));
        //? if < 1.21.11
        playerManager.load(player);
        return player;
    }

    public static void saveOfflinePlayer(ServerPlayer player) {
        if (isOnline(player.getUUID())) {
            Solstice.LOGGER.warn("Tried to save offline player data for a player that is online.");
            return;
        }
        var saveHandler = Solstice.server.playerDataStorage;
        saveHandler.save(player);
        Solstice.server.getPlayerList().remove(player);
    }

    public static void playSound(@UnknownNullability ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        //? < 1.21.11
        player.playNotifySound(sound, net.minecraft.sounds.SoundSource.MASTER, volume, pitch);
        //? >= 1.21.1
        player.playSound(sound, volume, pitch);

    }

    public static Optional<GameProfile> getProfile(MinecraftServer server, String targetName) {
        //? >= 1.21.11
        //return server.services().profileResolver().fetchByName(targetName);
        //? < 1.21.11
        return server.getProfileCache().get(targetName);
    }
        public static Optional<GameProfile> getProfile(MinecraftServer server, UUID id) {
        //? >= 1.21.11
        //return server.services().profileResolver().fetchById(id);
        //? < 1.21.11
        return server.getProfileCache().get(id);
    }

    //? >= 1.21.11 {
    /*public static UUID getId(net.minecraft.server.players.NameAndId nameAndId) {
        return nameAndId.id();
    }
    *///? }
    public static UUID getId(GameProfile gameProfile) {
        //? >= 1.21.11
        //return gameProfile.id();
        //? < 1.21.11
        return gameProfile.getId();
    }

    //? >= 1.21.11 {
    /*public static String getName(net.minecraft.server.players.NameAndId nameAndId) {
        return nameAndId.name();
    }
    *///? }
    public static String getName(GameProfile gameProfile) {
        //? >= 1.21.11
        //return gameProfile.name();
        //? < 1.21.11
        return gameProfile.getName();
    }

    public static ServerLevel getLevel(ServerPlayer player) {
        //? >= 1.21.11
        //return player.level();
        //? < 1.21.11
        return player.serverLevel();
    }
}
