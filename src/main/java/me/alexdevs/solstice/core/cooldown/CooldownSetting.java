package me.alexdevs.solstice.core.cooldown;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class CooldownSetting {
    public static CooldownSetting of(List<String> nodes, int cooldown) {
        var setting = new CooldownSetting();
        setting.nodes = nodes;
        setting.cooldown = cooldown;

        return setting;
    }

    public List<String> nodes = List.of();
    public int cooldown = 0;

    private String key;
    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
