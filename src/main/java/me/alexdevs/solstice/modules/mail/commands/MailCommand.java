package me.alexdevs.solstice.modules.mail.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.ModModuleProvider;
import me.alexdevs.solstice.modules.mail.data.PlayerMail;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.api.text.parser.MarkdownParser;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.alexdevs.solstice.modules.mail.MailModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MailCommand extends ModCommand<MailModule> {
    public MailCommand(MailModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("mail");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(this::listMails)
                .then(literal("send")
                        .then(argument("recipient", StringArgumentType.word())
                                .suggests(LocalGameProfile::suggest)
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(this::sendMail)
                                )
                        )
                )
                .then(literal("read")
                        .then(argument("index", IntegerArgumentType.integer(0))
                                .executes(this::readMail)))
                .then(literal("delete")
                        .then(argument("index", IntegerArgumentType.integer(0))
                                .executes(this::deleteMail)));
    }

    private int listMails(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var playerContext = PlaceholderContext.of(player);
        var mails = module.getMailList(player.getUUID());

        if (mails.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get("emptyMailbox", playerContext), false);
            return 0;
        }

        var output = Component.empty()
                .append(module.locale().get("mailListHeader", playerContext))
                .append(Component.nullToEmpty("\n"));

        for (var i = 0; i < mails.size(); i++) {
            if (i > 0)
                output = output.append(Component.nullToEmpty("\n"));

            var mail = mails.get(i);
            var index = i + 1;

            var readButton = Components.button(
                    module.locale().raw("readButton"),
                    module.locale().raw("hoverRead"),
                    "/mail read " + index
            );

            var senderName = CoreModule.getUsername(mail.sender);
            var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
            var placeholders = Map.of(
                    "index", Component.nullToEmpty(String.valueOf(index)),
                    "sender", Component.nullToEmpty(senderName),
                    "date", Component.nullToEmpty(dateFormatter.format(mail.date)),
                    "readButton", readButton
            );
            output = output.append(module.locale().get("mailListEntry", playerContext, placeholders));
        }

        final var finalOutput = output;

        context.getSource().sendSuccess(() -> finalOutput, false);

        return 1;
    }

    private int readMail(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var playerContext = PlaceholderContext.of(player);
        var mails = module.getMailList(player.getUUID());
        var index = IntegerArgumentType.getInteger(context, "index") - 1;

        if (index < 0 || index >= mails.size()) {
            context.getSource().sendSuccess(() -> module.locale().get("notFound"), false);
            return 0;
        }

        var mail = mails.get(index);

        var username = CoreModule.getUsername(mail.sender);

        var replyButton = Components.buttonSuggest(
                module.locale().raw("replyButton"),
                module.locale().raw("hoverReply"),
                "/mail send " + username + " "
        );
        var deleteButton = Components.button(
                module.locale().raw("deleteButton"),
                module.locale().raw("hoverDelete"),
                "/mail delete " + index + 1
        );

        var senderName = CoreModule.getUsername(mail.sender);
        var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
        var message = MarkdownParser.defaultParser.parseNode(mail.message);
        //? if >= 26.1
        //var messageComponent = message.toComponent();
        //? if < 26.1
        var messageComponent = message.toText();
        var placeholders = Map.of(
                "sender", Component.nullToEmpty(senderName),
                "date", Component.nullToEmpty(dateFormatter.format(mail.date)),
                "message", messageComponent,
                "replyButton", replyButton,
                "deleteButton", deleteButton
        );

        context.getSource().sendSuccess(() -> module.locale().get("mailDetails", playerContext, placeholders), false);

        return 1;
    }

    private int deleteMail(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        var playerContext = PlaceholderContext.of(player);
        var index = IntegerArgumentType.getInteger(context, "index") - 1;

        if (module.deleteMail(player.getUUID(), index)) {
            context.getSource().sendSuccess(() -> module.locale().get("mailDeleted", playerContext), false);
        } else {
            context.getSource().sendSuccess(() -> module.locale().get("notFound"), false);
        }

        return 1;
    }

    private int sendMail(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var sender = context.getSource().getPlayerOrException();
        var profile = LocalGameProfile.getProfile(context, "recipient");

        var message = StringArgumentType.getString(context, "message");
        var server = context.getSource().getServer();

        var mail = new PlayerMail(message, sender.getUUID());
        var actuallySent = module.sendMail(PlayerUtils.getId(profile), mail);

        var senderContext = PlaceholderContext.of(sender);

        context.getSource().sendSuccess(() -> module.locale().get("mailSent", senderContext), false);

        if (actuallySent) {
            var recPlayer = server.getPlayerList().getPlayer(PlayerUtils.getId(profile));
            if (recPlayer == null) {
                return 1;
            }

            if (ModModuleProvider.IGNORE.isIgnoring(recPlayer, sender))
                return 0;

            var recContext = PlaceholderContext.of(recPlayer);
            recPlayer.sendSystemMessage(module.locale().get("mailReceived", recContext));
        }

        return 1;
    }
}
