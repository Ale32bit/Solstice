package me.alexdevs.solstice.modules.customName;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.text.RawPlaceholder;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.customName.commands.NicknameCommand;
import me.alexdevs.solstice.modules.customName.data.CustomNameConfig;
import me.alexdevs.solstice.modules.customName.data.CustomNameLocale;
import me.alexdevs.solstice.modules.customName.data.CustomNamePlayerData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class CustomNameModule extends ModuleBase.Toggleable {
    public static final String ID = "customname";

    public static final Pattern BASIC_NICKNAME_FILTER = Pattern.compile("[^a-zA-Zà-üÀ-Ü_ ]");

    public CustomNameModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, CustomNameConfig.class, CustomNameConfig::new);
        Solstice.localeManager.registerModule(ID, CustomNameLocale.MODULE);
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

    public @Nullable String getCustomName(ServerPlayer player) {
        return getCustomName(player.getUUID());
    }

    public @Nullable String getCustomName(UUID uuid) {
        var playerData = Solstice.playerData.get(uuid).getData(CustomNamePlayerData.class);
        return playerData.nickname;
    }

    public void setCustomName(ServerPlayer player, String name) {
        setCustomName(player, name, true);
    }

    public void setCustomName(ServerPlayer player, String name, boolean advancedFormatting) {
        setCustomName(player.getUUID(), name, advancedFormatting);
    }

    public void setCustomName(UUID uuid, String name, boolean advancedFormatting) {
        if (!advancedFormatting) {
            name = BASIC_NICKNAME_FILTER.matcher(name).replaceAll("");
        }

        var playerData = Solstice.playerData.get(uuid).getData(CustomNamePlayerData.class);
        playerData.nickname = name;
    }

    public void clearCustomName(ServerPlayer player) {
        clearCustomName(player.getUUID());
    }

    public void clearCustomName(UUID uuid) {
        var playerData = Solstice.playerData.get(uuid).getData(CustomNamePlayerData.class);
        playerData.nickname = null;
    }
}
