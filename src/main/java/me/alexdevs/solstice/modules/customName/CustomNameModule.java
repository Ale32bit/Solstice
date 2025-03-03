package me.alexdevs.solstice.modules.customName;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.text.RawPlaceholder;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.customName.commands.NicknameCommand;
import me.alexdevs.solstice.modules.customName.data.CustomNameConfig;
import me.alexdevs.solstice.modules.customName.data.CustomNamePlayerData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;

public class CustomNameModule extends ModuleBase.Toggleable {
    public static final String ID = "customname";

    public CustomNameModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, CustomNameConfig.class, CustomNameConfig::new);
        Solstice.playerData.registerData(ID, CustomNamePlayerData.class, CustomNamePlayerData::new);

        commands.add(new NicknameCommand(this));
    }

    public String fetchUsernameFormat(ServerPlayer player) {
        var formats = Solstice.configManager.getData(CustomNameConfig.class).nameFormats;

        String format = null;
        for (var f : formats) {
            if (LuckPermsIntegration.isInGroup(player, f.group())) {
                format = f.format();
                break;
            }
        }

        var isOperator = player.getServer().getPlayerList().isOp(player.getGameProfile());

        if (format == null) {
            format = "${username}";

            for (var f : formats) {
                if (isOperator && f.group().equals("operator")) {
                    format = f.format();
                    break;
                }
                if (f.group().equals("default")) {
                    format = f.format();
                    break;
                }
            }
        }

        return format;
    }

    public String getResolvedUsername(ServerPlayer player) {
        var format = fetchUsernameFormat(player);
        var playerData = Solstice.playerData.get(player).getData(CustomNamePlayerData.class);
        var name = playerData.nickname == null ? player.getGameProfile().getName() : playerData.nickname;

        var prefix = LuckPermsIntegration.getPrefix(player);
        var suffix = LuckPermsIntegration.getSuffix(player);
        if (prefix == null)
            prefix = "";
        if (suffix == null)
            suffix = "";

        Map<String, String> placeholders = Map.of(
                "name", name,
                "prefix", prefix,
                "suffix", suffix
        );

        return RawPlaceholder.parse(format, placeholders);
    }

    public MutableComponent getNameForPlayer(ServerPlayer player) {
        var name = getResolvedUsername(player);
        var playerContext = PlaceholderContext.of(player);
        return Format.parse(name, playerContext).copy();
    }

    public void setCustomName(ServerPlayer player, String name) {
        var playerData = Solstice.playerData.get(player).getData(CustomNamePlayerData.class);
        playerData.nickname = name;
    }

    public void clearCustomName(ServerPlayer player) {
        var playerData = Solstice.playerData.get(player).getData(CustomNamePlayerData.class);
        playerData.nickname = null;
    }
}
