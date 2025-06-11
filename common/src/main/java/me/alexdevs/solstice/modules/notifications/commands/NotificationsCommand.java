package me.alexdevs.solstice.modules.notifications.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.notifications.NotificationsModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Map;

public class NotificationsCommand extends ModCommand<NotificationsModule> {

    public NotificationsCommand(NotificationsModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("notifications");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(true))
                .then(Commands.literal("set")
                        .then(Commands.literal("sound")
                                .then(Commands.argument("sound", ResourceLocationArgument.id())
                                        .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                        .executes(this::setSound)
                                )
                        )
                        .then(Commands.literal("pitch")
                                .then(Commands.argument("pitch", FloatArgumentType.floatArg(0f, 2f))
                                        .executes(this::setPitch)
                                )
                        )
                        .then(Commands.literal("volume")
                                .then(Commands.argument("volume", IntegerArgumentType.integer(0, 200))
                                        .executes(this::setVolume)
                                )
                        )
                        .then(Commands.literal("afk-only")
                                .then(Commands.argument("afk-only", BoolArgumentType.bool())
                                        .executes(this::setAfkOnly)
                                )
                        )
                        .then(Commands.literal("on-chat")
                                .then(Commands.argument("on-chat", BoolArgumentType.bool())
                                        .executes(this::setOnChat)
                                )
                        )
                )
                .then(Commands.literal("get")
                        .executes(this::getSettings))
                .then(Commands.literal("toggle")
                        .executes(this::toggle))
                .then(Commands.literal("reset")
                        .executes(this::reset));
    }

    private int setSound(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var soundId = ResourceLocationArgument.getId(context, "sound");

        var data = module.getPlayerData(player);
        data.soundId = soundId.toString();

        var map = Map.of(
                "sound", Component.nullToEmpty(soundId.toString())
        );
        context.getSource().sendSuccess(() -> module.locale().get("setSound", map), false);

        return 1;
    }

    private int setPitch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var pitch = FloatArgumentType.getFloat(context, "pitch");

        var data = module.getPlayerData(player);
        data.pitch = pitch;

        var map = Map.of(
                "pitch", Component.nullToEmpty(String.valueOf(pitch))
        );
        context.getSource().sendSuccess(() -> module.locale().get("setPitch", map), false);

        return 1;
    }

    private int setVolume(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var volume = IntegerArgumentType.getInteger(context, "volume");

        var data = module.getPlayerData(player);
        data.volume = volume / 100f;

        var map = Map.of(
                "volume", Component.nullToEmpty(volume + "%")
        );
        context.getSource().sendSuccess(() -> module.locale().get("setVolume", map), false);

        return 1;
    }

    private int setAfkOnly(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var afkOnly = BoolArgumentType.getBool(context, "afk-only");

        var data = module.getPlayerData(player);
        data.afkOnly = afkOnly;

        if (afkOnly) {
            context.getSource().sendSuccess(() -> module.locale().get("setAfkOnlyEnabled"), false);
        } else {
            context.getSource().sendSuccess(() -> module.locale().get("setAfkOnlyDisabled"), false);
        }

        return 1;
    }

    private int setOnChat(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var onChat = BoolArgumentType.getBool(context, "on-chat");

        var data = module.getPlayerData(player);
        data.onChat = onChat;

        if (onChat) {
            context.getSource().sendSuccess(() -> module.locale().get("setOnChatEnabled"), false);
        } else {
            context.getSource().sendSuccess(() -> module.locale().get("setOnChatDisabled"), false);
        }

        return 1;
    }

    private int getSettings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var data = module.getPlayerData(player);
        var settings = module.getPlayerSettings(player);

        var map = Map.of(
                "sound", Component.nullToEmpty(settings.soundId()),
                "pitch", Component.nullToEmpty(String.valueOf(settings.pitch())),
                "volume", Component.nullToEmpty(settings.volume() * 100 + "%")
        );

        var text = Component.empty();
        text.append(module.locale().get("getHeader"));
        text.append("\n");

        text.append(module.locale().get(data.enable ? "getEnabled.true" : "getEnabled.false"));
        text.append("\n");
        text.append(module.locale().get("getSound", map));
        text.append("\n");
        text.append(module.locale().get("getPitch", map));
        text.append("\n");
        text.append(module.locale().get("getVolume", map));
        text.append("\n");
        text.append(module.locale().get(settings.afkOnly() ? "getAfkOnly.true" : "getAfkOnly.false"));
        text.append("\n");
        text.append(module.locale().get(settings.onChat() ? "getOnChat.true" : "getOnChat.false"));

        context.getSource().sendSuccess(() -> text, false);

        return 1;
    }

    private int toggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var data = module.getPlayerData(player);
        data.enable = !data.enable;

        if (data.enable) {
            context.getSource().sendSuccess(() -> module.locale().get("toggleEnabled"), false);
        } else {
            context.getSource().sendSuccess(() -> module.locale().get("toggleDisabled"), false);
        }

        return 1;
    }

    private int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var data = module.getPlayerData(player);
        data.soundId = null;
        data.pitch = null;
        data.volume = null;
        data.afkOnly = null;

        context.getSource().sendSuccess(() -> module.locale().get("reset"), false);

        return 1;
    }
}
