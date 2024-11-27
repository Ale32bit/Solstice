package me.alexdevs.solstice.api;

import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.UUID;

public class PlayerMail {
    @Expose
    public String message;
    @Expose
    public UUID sender;
    @Expose
    public Date date;

    public PlayerMail(String message, UUID sender) {
        this.message = message;
        this.sender = sender;
        this.date = new Date();
    }
}
