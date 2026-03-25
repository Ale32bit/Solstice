package me.alexdevs.solstice.modules.styling;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import java.util.Collection;
import java.util.List;


//? if >= 1.21.1 {
import net.minecraft.network.chat.TextColor;
import java.util.Map;
import java.util.stream.Collectors;
//? } else {
/*import net.minecraft.MethodsReturnNonnullByDefault;
@MethodsReturnNonnullByDefault
*///? }

public class CustomPlayerTeam extends PlayerTeam {

    private final ServerPlayer player;

    public CustomPlayerTeam(Scoreboard scoreboard, ServerPlayer player) {
        super(scoreboard, "sol_" + PlayerUtils.getName(player.getGameProfile()));
        this.player = player;
        super.getPlayers().add(PlayerUtils.getName(player.getGameProfile()));
    }
//? if >= 1.21.1 {

    private final static Map<TextColor, ChatFormatting> COLOR_MAP = TextColor.NAMED_COLORS.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, entry -> textColorToFormatting(entry.getKey())));

    private static ChatFormatting textColorToFormatting(String name) {
        var value = ChatFormatting.getByName(name);
        if (value == null) return ChatFormatting.WHITE;
        return value;
    }

    private static ChatFormatting getFormatting(TextColor color) {
        return COLOR_MAP.getOrDefault(color, ChatFormatting.WHITE);
    }

    @Override
    public ChatFormatting getColor() {
        if (ModuleProvider.STYLING.shouldColorNameplate()) {
            return getFormatting(player.getDisplayName().getStyle().getColor());
        }
        return super.getColor();
    }

//? } else {
    /*@Override
    public Component getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public ChatFormatting getColor() {
        return ModuleProvider.STYLING.getNameplateColor(player);
    }

*///? }

    @Override
    public Component getPlayerPrefix() {
        return ModuleProvider.STYLING.getNameplatePrefix(player);
    }

    @Override
    public Component getPlayerSuffix() {
        return ModuleProvider.STYLING.getNameplateSuffix(player);
    }

    @Override
    public Collection<String> getPlayers() {
        return List.of(PlayerUtils.getName(player.getGameProfile()));
    }
}