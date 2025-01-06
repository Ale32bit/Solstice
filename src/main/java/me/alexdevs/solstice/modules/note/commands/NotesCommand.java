package me.alexdevs.solstice.modules.note.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.core.CoreModule;
import me.alexdevs.solstice.modules.note.NoteModule;
import me.alexdevs.solstice.modules.note.data.Note;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NotesCommand extends ModCommand<NoteModule> {
    public NotesCommand(NoteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("notes", "note");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("user", GameProfileArgumentType.gameProfile())
                        .executes(this::listNotes)
                        .then(literal("add")
                                .requires(require("add", 2))
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes(this::addNote)
                                )
                        )
                        .then(literal("check")
                                .then(argument("index", IntegerArgumentType.integer(0))
                                        .executes(this::checkNote)))
                        .then(literal("delete")
                                .requires(require("delete", 2))
                                .then(argument("index", IntegerArgumentType.integer(0))
                                        .executes(this::deleteNote)))
                        .then(literal("clear")
                                .requires(require("clear", 2))
                                .executes(this::clearNotes))
                );
    }

    private int listNotes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var user = getUser(context);
        var notes = module.getNotes(user.getId());

        if (notes.isEmpty()) {
            context.getSource().sendFeedback(() -> module.locale().get("emptyNotes"), false);
            return 0;
        }

        var output = Text.empty()
                .append(module.locale().get("noteListHeader", Map.of(
                        "user", Text.of(user.getName())
                )))
                .append(Text.of("\n"));

        for (var i = 0; i < notes.size(); i++) {
            if (i > 0)
                output = output.append(Text.of("\n"));

            var note = notes.get(i);
            var index = i + 1;

            var checkButton = Components.button(
                    module.locale().raw("checkButton"),
                    module.locale().raw("hoverCheck"),
                    "/notes " + user.getName() + " check " + index
            );

            var senderName = CoreModule.getUsername(note.createdBy);
            var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
            var placeholders = Map.of(
                    "index", Text.of(String.valueOf(index)),
                    "operator", Text.of(senderName),
                    "date", Text.of(dateFormatter.format(note.creationDate)),
                    "message", Format.parse(note.note),
                    "checkButton", checkButton
            );
            output = output.append(module.locale().get("noteListEntry", placeholders));
        }

        final var finalOutput = output;

        context.getSource().sendFeedback(() -> finalOutput, false);

        return 1;
    }

    private int checkNote(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var user = getUser(context);
        var notes = module.getNotes(user.getId());
        var index = IntegerArgumentType.getInteger(context, "index") - 1;

        if (index < 0 || index >= notes.size()) {
            context.getSource().sendFeedback(() -> module.locale().get("notFound"), false);
            return 0;
        }

        var note = notes.get(index);

        var deleteButton = Components.button(
                module.locale().raw("deleteButton"),
                module.locale().raw("hoverDelete"),
                "/note " + user.getName() + " delete " + index + 1
        );

        var operator = CoreModule.getUsername(note.createdBy);
        var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
        var placeholders = Map.of(
                "operator", Text.of(operator),
                "date", Text.of(dateFormatter.format(note.creationDate)),
                "message", Format.parse(note.note),
                "deleteButton", deleteButton
        );

        context.getSource().sendFeedback(() -> module.locale().get("noteDetails", placeholders), false);

        return 1;
    }

    private int deleteNote(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var user = getUser(context);
        var notes = module.getNotes(user.getId());
        var index = IntegerArgumentType.getInteger(context, "index") - 1;

        if (index < notes.size()) {
            notes.remove(index);
            context.getSource().sendFeedback(() -> module.locale().get("noteDeleted"), false);
        } else {
            context.getSource().sendFeedback(() -> module.locale().get("notFound"), false);
            return 0;
        }

        return 1;
    }

    private int addNote(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var user = getUser(context);

        UUID operatorId = new UUID(0, 0);
        if(context.getSource().isExecutedByPlayer())
            operatorId = context.getSource().getPlayer().getUuid();

        var message = StringArgumentType.getString(context, "message");

        var note = new Note(message, operatorId);
        var notes = module.getNotes(user.getId());

        notes.add(note);
        var index = notes.size() - 1;

        context.getSource().sendFeedback(() -> module.locale().get("noteAdded"), false);

        var checkButton = Components.button(
                module.locale().raw("checkButton"),
                module.locale().raw("hoverCheck"),
                "/notes " + user.getName() + "check " + index
        );
        final var text = module.locale().get("addedNotification", Map.of(
                "operator", context.getSource().getDisplayName(),
                "user", Text.of(user.getName()),
                "checkButton", checkButton
        ));

        context.getSource().getServer().getPlayerManager().getPlayerList().forEach(pl -> {
            if (Permissions.check(pl, getPermissionNode("notify"), 2)) {
                pl.sendMessage(text);
            }
        });

        return 1;
    }

    private int clearNotes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var user = getUser(context);

        var notes = module.getNotes(user.getId());
        notes.clear();

        context.getSource().sendFeedback(() -> module.locale().get("notesCleared", Map.of(
                "user", Text.of(user.getName())
        )), true);

        return 1;
    }

    private GameProfile getUser(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var profiles = GameProfileArgumentType.getProfileArgument(context, "user");
        if (profiles.size() > 1) {
            throw EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION.create();
        }

        return profiles.iterator().next();
    }
}
