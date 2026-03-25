package me.alexdevs.solstice.api.utils;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class ProfileOrNameAndId {

    private com.mojang.authlib.GameProfile profile;

    //? if >= 1.21.11
    //private net.minecraft.server.players.NameAndId nameAndId;
    //? if < 1.21.11
    private GameProfile nameAndId;

    public ProfileOrNameAndId(com.mojang.authlib.GameProfile profile) {
        this.profile = profile;
        //? if >= 1.21.11
        //this.nameAndId = new net.minecraft.server.players.NameAndId(profile.id(), profile.name());
        //? if < 1.21.11
        this.nameAndId = profile;
    }

    //? if >= 1.21.11 {
    /*public ProfileOrNameAndId(net.minecraft.server.players.NameAndId nameAndId) {
        this.nameAndId = nameAndId;
        this.profile = new com.mojang.authlib.GameProfile(nameAndId.id(), nameAndId.name());
    }
    *///? }

    //? if >= 1.21.11
    //public net.minecraft.server.players.NameAndId getNameAndId() {
    //? if < 1.21.11
    public GameProfile getNameAndId() {
        return nameAndId;
    }
    public com.mojang.authlib.GameProfile getProfile() {
        return profile;
    }

    public String getName() {
        if (profile != null)
            //? if < 1.21.11
            return profile.getName();
            //? if >= 1.21.11
            //return profile.name();

        if (nameAndId!=null) {
            //? if < 1.21.11
            return nameAndId.getName();
            //? if >= 1.21.11
            //return nameAndId.name();
        }
        return null;
    }

    public UUID getId() {
        if (profile != null)
            //? if < 1.21.11
            return profile.getId();
            //? if >= 1.21.11
            //return profile.id();

        if (nameAndId != null) {
            //? if < 1.21.11
            return nameAndId.getId();
            //? if >= 1.21.11
            //return nameAndId.id();
        }
        return null;
    }
}
