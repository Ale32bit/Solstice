package me.alexdevs.solstice.api.utils;

import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
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
        *///? }
        //? if >= 1.21.1 && < 1.21.11 {
        var player = playerManager.getPlayerForLogin(profile, ClientInformation.createDefault());
        //? }


        //? if >= 1.21.11
        //Solstice.server.playerDataStorage.load(new net.minecraft.server.players.NameAndId(profile.id(), profile.name()));
        //? if < 1.21.11
        playerManager.load(player);
        return player;
    }

    private static final java.util.Set<String> SAVE_KEYS = java.util.Set.of("Inventory", "equipment","EnderItems",
            //? if >= 1.21.11
            //"trinkets"
            //? if < 1.21.11
            "cardinal_components"
    );

    public static void saveOfflinePlayer(ServerPlayer player) {
        if (isOnline(player.getUUID())) {
            Solstice.LOGGER.warn("Tried to save offline player data for a player that is online.");
            return;
        }

        var server = Solstice.server;
        var storage = server.playerDataStorage;
        var uuid = player.getUUID();

        CompoundTag original;
        CompoundTag fresh;

        //? if >=1.21.11 {
        /*original = storage.load(new net.minecraft.server.players.NameAndId(uuid, player.getGameProfile().name())).orElse(null);
        if (original == null) {
            storage.save(player);
            return;
        }
        var out = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, server.registryAccess());
        player.saveWithoutId(out);
        fresh = out.buildResult();
        *///?} else {
        var datFile = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(uuid + ".dat");
        if (!Files.exists(datFile)) {
            storage.save(player);
            return;
        }
        try {
            var raw = NbtIo.readCompressed(datFile, NbtAccounter.unlimitedHeap());
            original = DataFixTypes.PLAYER.updateToCurrentVersion(server.getFixerUpper(), raw, NbtUtils.getDataVersion(raw, -1));
        }
        catch (Exception e) {
            Solstice.LOGGER.warn("Failed to read offline player {}", uuid, e);
            return;
        }
        fresh = new CompoundTag();
        player.saveWithoutId(fresh);
        //?}

        for (var key : SAVE_KEYS) {
            var tag = fresh.get(key);
            if (tag != null) {
                original.put(key, tag);
            }
            else {
                original.remove(key);
            }
        }

        try {
            NbtUtils.addCurrentDataVersion(original);
            var dir = server.getWorldPath(LevelResource.PLAYER_DATA_DIR);
            var dat = dir.resolve(uuid + ".dat");
            var old = dir.resolve(uuid + ".dat_old");
            var tmp = Files.createTempFile(dir, uuid + "-", ".dat");
            NbtIo.writeCompressed(original, tmp);
            replaceFile(dat, tmp, old);
        }
        catch (Exception e) {
            Solstice.LOGGER.warn("Failed to save offline player {}", uuid, e);
        }
    }

    private static void replaceFile(Path dat, Path tmp, Path old) throws java.io.IOException {
        if (Files.exists(dat)) {
            Files.copy(dat, old, StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            Files.move(tmp, dat, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, dat, StandardCopyOption.REPLACE_EXISTING);
        }
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
