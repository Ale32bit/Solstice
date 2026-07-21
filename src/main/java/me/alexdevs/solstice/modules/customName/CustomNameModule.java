package me.alexdevs.solstice.modules.customName;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.api.text.RawPlaceholder;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.api.utils.ProfileOrNameAndId;
import me.alexdevs.solstice.integrations.LuckPermsIntegration;
import me.alexdevs.solstice.modules.customName.commands.NicknameCommand;
import me.alexdevs.solstice.modules.customName.data.CustomNameConfig;
import me.alexdevs.solstice.modules.customName.data.CustomNameLocale;
import me.alexdevs.solstice.modules.customName.data.CustomNamePlayerData;
import net.minecraft.network.chat.MutableComponent;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class CustomNameModule extends ModuleBase {
    public CustomNameModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerConfig(CustomNameConfig.class, CustomNameConfig::new);
        registerLocale(CustomNameLocale.MODULE);
        registerPlayerData(CustomNamePlayerData.class, CustomNamePlayerData::new);

        commands.add(new NicknameCommand(this));
    }

    public CustomNameConfig getConfig() {
        return Solstice.configManager.getData(CustomNameConfig.class);
    }

    public Pattern getNicknameFilter() {
        return Pattern.compile(getConfig().basicFilter);
    }

    public String fetchUsernameFormat(ServerPlayer player) {
        var formats = getConfig().nameFormats;

        String format = null;
        for (var f : formats) {
            if (LuckPermsIntegration.isInGroup(player, f.group())) {
                format = f.format();
                break;
            }
        }

        var isOperator = player.getServer().getPlayerList().isOp(new ProfileOrNameAndId(player.getGameProfile()).getNameAndId());

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
        var name = playerData.nickname == null ? PlayerUtils.getName(player.getGameProfile()) : playerData.nickname;

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

    public boolean setCustomName(ServerPlayer player, String name, boolean advancedFormatting) {
        return setCustomName(player.getUUID(), name, advancedFormatting);
    }

    public boolean setCustomName(UUID uuid, String name, boolean advancedFormatting) {
        if (!advancedFormatting) {
            var config = getConfig();
            name = getNicknameFilter().matcher(name).replaceAll("");

            if (name.length() < config.minSafeNicknameLength) {
                return false;
            }

            if (name.length() > config.maxSafeNicknameLength) {
                name = name.substring(0, config.maxSafeNicknameLength);
            }
        }

        var playerData = Solstice.playerData.get(uuid).getData(CustomNamePlayerData.class);
        playerData.nickname = name;

        return true;
    }

    public void clearCustomName(ServerPlayer player) {
        clearCustomName(player.getUUID());
    }

    public void clearCustomName(UUID uuid) {
        var playerData = Solstice.playerData.get(uuid).getData(CustomNamePlayerData.class);
        playerData.nickname = null;
    }
}
