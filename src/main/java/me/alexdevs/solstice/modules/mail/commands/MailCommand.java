package me.alexdevs.solstice.modules.mail.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.PlayerMail;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.core.CoreModule;
import me.alexdevs.solstice.modules.mail.MailModule;
import me.alexdevs.solstice.modules.moderation.ModerationModule;
import me.alexdevs.solstice.util.Components;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.util.parser.MarkdownParser;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.*;

public class MailCommand extends ModCommand {
    private final Locale locale = Solstice.localeManager.getLocale(MailModule.ID);
    private final MailModule mailModule = Solstice.modules.mail;

    public MailCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistry, RegistrationEnvironment environment) {
        super(dispatcher, commandRegistry, environment);
    }

    @Override
    public List<String> getNames() {
        return List.of("mail");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .executes(this::listMails)
                .then(literal("send")
                        .then(argument("recipient", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    var playerManager = context.getSource().getServer().getPlayerManager();
                                    return CommandSource.suggestMatching(
                                            playerManager.getPlayerNames(),
                                            builder);
                                })
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

    private int listMails(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var playerContext = PlaceholderContext.of(player);
        var mails = mailModule.getMailList(player.getUuid());

        if(mails.isEmpty()) {
            context.getSource().sendFeedback(() -> locale.get("emptyMailbox", playerContext), false);
            return 1;
        }

        var output = Text.empty()
                .append(locale.get("mailListHeader", playerContext))
                .append(Text.of("\n"));

        for (var i = 0; i < mails.size(); i++) {
            if (i > 0)
                output = output.append(Text.of("\n"));

            var mail = mails.get(i);
            var index = i + 1;

            var readButton = Components.button(
                    locale.raw("readButton"),
                    locale.raw("hoverRead"),
                    "/mail read " + index
            );

            var senderName = CoreModule.getUsername(mail.sender);
            var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
            var placeholders = Map.of(
                    "index", Text.of(String.valueOf(index)),
                    "sender", Text.of(senderName),
                    "date", Text.of(dateFormatter.format(mail.date)),
                    "readButton", readButton
            );
            output = output.append(locale.get("mailListEntry", playerContext, placeholders));
        }

        final var finalOutput = output;

        context.getSource().sendFeedback(() -> finalOutput, false);

        return 1;
    }

    private int readMail(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var playerContext = PlaceholderContext.of(player);
        var mails = mailModule.getMailList(player.getUuid());
        var index = IntegerArgumentType.getInteger(context, "index") - 1;

        if (index < 0 || index >= mails.size()) {
            context.getSource().sendFeedback(() -> locale.get("notFound"), false);
            return 1;
        }

        var mail = mails.get(index);

        var username = CoreModule.getUsername(mail.sender);

        var replyButton = Components.buttonSuggest(
                locale.raw("replyButton"),
                locale.raw("hoverReply"),
                "/mail send " + username + " "
        );
        var deleteButton = Components.button(
                locale.raw("deleteButton"),
                locale.raw("hoverDelete"),
                "/mail delete " + index + 1
        );

        var senderName = CoreModule.getUsername(mail.sender);
        var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
        var message = MarkdownParser.defaultParser.parseNode(mail.message);
        var placeholders = Map.of(
                "sender", Text.of(senderName),
                "date", Text.of(dateFormatter.format(mail.date)),
                "message", message.toText(),
                "replyButton", replyButton,
                "deleteButton", deleteButton
        );

        context.getSource().sendFeedback(() -> locale.get("mailDetails", playerContext, placeholders), false);

        return 1;
    }

    private int deleteMail(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrThrow();
        var playerContext = PlaceholderContext.of(player);
        var index = IntegerArgumentType.getInteger(context, "index") - 1;

        if (mailModule.deleteMail(player.getUuid(), index)) {
            context.getSource().sendFeedback(() -> locale.get("mailDeleted", playerContext), false);
        } else {
            context.getSource().sendFeedback(() -> locale.get("notFound"), false);
        }

        return 1;
    }

    private int sendMail(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var sender = context.getSource().getPlayerOrThrow();
        var username = StringArgumentType.getString(context, "recipient");
        context.getSource().getServer().getUserCache().findByNameAsync(username, gameProfile -> {
            if (gameProfile.isEmpty()) {
                var playerContext = PlaceholderContext.of(sender);

                var placeholders = Map.of(
                        "recipient", Text.of(username)
                );

                context.getSource().sendFeedback(() -> locale.get("playerNotFound", playerContext, placeholders), false);
                return;
            }

            var message = StringArgumentType.getString(context, "message");
            var recipient = gameProfile.get();
            var server = context.getSource().getServer();

            var mail = new PlayerMail(message, sender.getUuid());
            var actuallySent = mailModule.sendMail(recipient.getId(), mail);

            var senderContext = PlaceholderContext.of(sender);

            context.getSource().sendFeedback(() -> locale.get("mailSent", senderContext), false);

            if(actuallySent) {
                var recPlayer = server.getPlayerManager().getPlayer(recipient.getId());
                if (recPlayer == null) {
                    return;
                }

                if(ModerationModule.isIgnoring(recPlayer, sender))
                    return;

                var recContext = PlaceholderContext.of(recPlayer);
                recPlayer.sendMessage(locale.get("mailReceived", recContext));
            }
        });

        return 1;
    }
}
