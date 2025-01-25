package me.alexdevs.solstice.modules.notifications.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.notifications.NotificationsModule;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require(true))
                .then(CommandManager.literal("set")
                        .then(CommandManager.literal("sound")
                                .then(CommandManager.argument("sound", IdentifierArgumentType.identifier())
                                        .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                        .executes(this::setSound)
                                )
                        )
                        .then(CommandManager.literal("pitch")
                                .then(CommandManager.argument("pitch", FloatArgumentType.floatArg(0f, 2f))
                                        .executes(this::setPitch)
                                )
                        )
                        .then(CommandManager.literal("volume")
                                .then(CommandManager.argument("volume", IntegerArgumentType.integer(0, 200))
                                        .executes(this::setVolume)
                                )
                        )
                        .then(CommandManager.literal("afk-only")
                                .then(CommandManager.argument("afk-only", BoolArgumentType.bool())
                                        .executes(this::setAfkOnly)
                                )
                        )
                        .then(CommandManager.literal("on-chat")
                                .then(CommandManager.argument("on-chat", BoolArgumentType.bool())
                                        .executes(this::setOnChat)
                                )
                        )
                )
                .then(CommandManager.literal("get")
                        .executes(this::getSettings))
                .then(CommandManager.literal("toggle")
                        .executes(this::toggle))
                .then(CommandManager.literal("reset")
                        .executes(this::reset));
    }

    private int setSound(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var soundId = IdentifierArgumentType.getIdentifier(context, "sound");

        var data = module.getPlayerData(player);
        data.soundId = soundId.toString();

        var map = Map.of(
                "sound", Text.of(soundId.toString())
        );
        context.getSource().sendFeedback(() -> module.locale().get("setSound", map), false);

        return 1;
    }

    private int setPitch(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var pitch = FloatArgumentType.getFloat(context, "pitch");

        var data = module.getPlayerData(player);
        data.pitch = pitch;

        var map = Map.of(
                "pitch", Text.of(String.valueOf(pitch))
        );
        context.getSource().sendFeedback(() -> module.locale().get("setPitch", map), false);

        return 1;
    }

    private int setVolume(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var volume = IntegerArgumentType.getInteger(context, "volume");

        var data = module.getPlayerData(player);
        data.volume = volume / 100f;

        var map = Map.of(
                "volume", Text.of(volume + "%")
        );
        context.getSource().sendFeedback(() -> module.locale().get("setVolume", map), false);

        return 1;
    }

    private int setAfkOnly(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var afkOnly = BoolArgumentType.getBool(context, "afk-only");

        var data = module.getPlayerData(player);
        data.afkOnly = afkOnly;

        if (afkOnly) {
            context.getSource().sendFeedback(() -> module.locale().get("setAfkOnlyEnabled"), false);
        } else {
            context.getSource().sendFeedback(() -> module.locale().get("setAfkOnlyDisabled"), false);
        }

        return 1;
    }

    private int setOnChat(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var onChat = BoolArgumentType.getBool(context, "on-chat");

        var data = module.getPlayerData(player);
        data.onChat = onChat;

        if (onChat) {
            context.getSource().sendFeedback(() -> module.locale().get("setOnChatEnabled"), false);
        } else {
            context.getSource().sendFeedback(() -> module.locale().get("setOnChatDisabled"), false);
        }

        return 1;
    }

    private int getSettings(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();

        var data = module.getPlayerData(player);
        var settings = module.getPlayerSettings(player);

        var map = Map.of(
                "sound", Text.of(settings.soundId()),
                "pitch", Text.of(String.valueOf(settings.pitch())),
                "volume", Text.of(settings.volume() * 100 + "%")
        );

        var text = Text.empty();
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

        context.getSource().sendFeedback(() -> text, false);

        return 1;
    }

    private int toggle(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();

        var data = module.getPlayerData(player);
        data.enable = !data.enable;

        if (data.enable) {
            context.getSource().sendFeedback(() -> module.locale().get("toggleEnabled"), false);
        } else {
            context.getSource().sendFeedback(() -> module.locale().get("toggleDisabled"), false);
        }

        return 1;
    }

    private int reset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();

        var data = module.getPlayerData(player);
        data.soundId = null;
        data.pitch = null;
        data.volume = null;
        data.afkOnly = null;

        context.getSource().sendFeedback(() -> module.locale().get("reset"), false);

        return 1;
    }
}
