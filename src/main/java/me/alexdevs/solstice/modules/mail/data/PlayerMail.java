package me.alexdevs.solstice.modules.mail.data;

import com.google.gson.annotations.Expose;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Date;
import java.util.UUID;

public class PlayerMail {
    public String message;
    public UUID sender;
    public Date date;

    public PlayerMail(String message, UUID sender) {
        this.message = message;
        this.sender = sender;
        this.date = new Date();
    }
}
