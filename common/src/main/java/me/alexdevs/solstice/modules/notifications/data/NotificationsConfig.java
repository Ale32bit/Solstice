package me.alexdevs.solstice.modules.notifications.data;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class NotificationsConfig {
    public DefaultValues defaultValues = new DefaultValues();

    @ConfigSerializable
    public static class DefaultValues {
        public String soundId = "minecraft:block.note_block.bell";
        public float pitch = 1f;
        public float volume = 1f;
        public boolean afkOnly = true;
        public boolean onChat = true;
    }
}
