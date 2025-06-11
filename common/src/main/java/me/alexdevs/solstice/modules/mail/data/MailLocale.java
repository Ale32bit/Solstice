package me.alexdevs.solstice.modules.mail.data;

import java.util.Map;

public class MailLocale {
    public static final Map<String, String> MODULE = Map.ofEntries(
            Map.entry("mailPending", "<green>You have pending mails! Run <click:run_command:'/mail'><hover:show_text:Click to read mails><aqua>/mail</aqua></hover></click> to read your mails.</green>"),
            Map.entry("replyButton", "<gold>Reply</gold>"),
            Map.entry("deleteButton", "<red>Delete</red>"),
            Map.entry("readButton", "<gold>Read</gold>"),
            Map.entry("hoverReply", "Click to reply to the mail"),
            Map.entry("hoverDelete", "Click to delete the mail"),
            Map.entry("hoverRead", "Click to read the mail"),
            Map.entry("playerNotFound", "<red>Player <yellow>${recipient}</yellow> not found!</red>"),
            Map.entry("mailSent", "<gold>Mail sent!</gold>"),
            Map.entry("mailReceived", "<gold>You received a new mail! Run <click:run_command:'/mail'><hover:show_text:Click to read mails><aqua>/mail</aqua></hover></click> to read the emails!</gold>"),
            Map.entry("mailDetails", "<gold>From</gold> <yellow>${sender}</yellow> <gold>on</gold> <yellow>${date}</yellow>\n ${message}\n\n ${replyButton} ${deleteButton}"),
            Map.entry("mailListHeader", "<gold>Your mails:</gold>"),
            Map.entry("mailListEntry", "<yellow>${index}.</yellow> <gold>From</gold> <yellow>${sender}</yellow> <gold>on</gold> <yellow>${date}</yellow> ${readButton}"),
            Map.entry("notFound", "<red>Mail not found</red>"),
            Map.entry("mailDeleted", "<gold>Mail deleted!</gold>"),
            Map.entry("emptyMailbox", "<gold>Your mailbox is empty.</gold>")
    );
}
