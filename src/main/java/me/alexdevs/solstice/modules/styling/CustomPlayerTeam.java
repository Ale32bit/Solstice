//? if >= 1.21.1 {
/*package me.alexdevs.solstice.modules.styling;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class CustomPlayerTeam extends PlayerTeam {
    private final static Map<TextColor, ChatFormatting> COLOR_MAP = TextColor.NAMED_COLORS.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, entry -> textColorToFormatting(entry.getKey())));
    private static ChatFormatting textColorToFormatting(String name) {
        var value = ChatFormatting.getByName(name);
        if (value == null) return ChatFormatting.WHITE;
        return value;
    }
    private static ChatFormatting getFormatting(TextColor color) {
        return COLOR_MAP.getOrDefault(color, ChatFormatting.WHITE);
    }
    private final ServerPlayer player;
    public CustomPlayerTeam(Scoreboard scoreboard, ServerPlayer player) {
        super(scoreboard, "sol_" + player.getGameProfile().getName());
        this.player = player;
        super.getPlayers().add(player.getGameProfile().getName());
    }
    @Override
    public ChatFormatting getColor() {
        if (ModuleProvider.STYLING.shouldColorNameplate()) {
            return getFormatting(player.getDisplayName().getStyle().getColor());
        }
        return super.getColor();
    }
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
        return List.of(player.getGameProfile().getName());
    }
}
*///? } else {
package me.alexdevs.solstice.modules.styling;
import me.alexdevs.solstice.modules.ModuleProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import java.util.Collection;
import java.util.List;
@MethodsReturnNonnullByDefault
public class CustomPlayerTeam extends PlayerTeam {
    private final ServerPlayer player;
    public CustomPlayerTeam(Scoreboard scoreboard, ServerPlayer player) {
        super(scoreboard, player.getGameProfile().getName());
        this.player = player;
        super.getPlayers().add(player.getGameProfile().getName());
    }
    @Override
    public Component getDisplayName() {
        return player.getDisplayName();
    }
    @Override
    public ChatFormatting getColor() {
        return ModuleProvider.STYLING.getNameplateColor(player);
    }
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
        return List.of(player.getGameProfile().getName());
    }
}
//? }
