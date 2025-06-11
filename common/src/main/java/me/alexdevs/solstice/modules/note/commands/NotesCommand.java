package me.alexdevs.solstice.modules.note.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.permissions.Permissions;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.alexdevs.solstice.modules.note.NoteModule;
import me.alexdevs.solstice.modules.note.data.Note;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class NotesCommand extends ModCommand<NoteModule> {
    public NotesCommand(NoteModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("notes", "note");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("user", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
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

    private int listNotes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var user = LocalGameProfile.getProfile(context, "user");
        var notes = module.getNotes(user.getId());

        if (notes.isEmpty()) {
            context.getSource().sendSuccess(() -> module.locale().get("emptyNotes"), false);
            return 0;
        }

        var output = Component.empty()
                .append(module.locale().get("noteListHeader", Map.of(
                        "user", Component.nullToEmpty(user.getName())
                )))
                .append(Component.nullToEmpty("\n"));

        for (var i = 0; i < notes.size(); i++) {
            if (i > 0)
                output = output.append(Component.nullToEmpty("\n"));

            var note = notes.get(i);

            var checkButton = Components.button(
                    module.locale().raw("checkButton"),
                    module.locale().raw("hoverCheck"),
                    "/notes " + user.getName() + " check " + i
            );

            var senderName = CoreModule.getUsername(note.createdBy);
            var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
            var placeholders = Map.of(
                    "index", Component.nullToEmpty(String.valueOf(i)),
                    "operator", Component.nullToEmpty(senderName),
                    "date", Component.nullToEmpty(dateFormatter.format(note.creationDate)),
                    "message", Format.parse(note.note),
                    "checkButton", checkButton
            );
            output = output.append(module.locale().get("noteListEntry", placeholders));
        }

        final var finalOutput = output;

        context.getSource().sendSuccess(() -> finalOutput, false);

        return 1;
    }

    private int checkNote(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var user = LocalGameProfile.getProfile(context, "user");
        var notes = module.getNotes(user.getId());
        var index = IntegerArgumentType.getInteger(context, "index");

        if (index < 0 || index >= notes.size()) {
            context.getSource().sendSuccess(() -> module.locale().get("notFound"), false);
            return 0;
        }

        var note = notes.get(index);

        var deleteButton = Components.button(
                module.locale().raw("deleteButton"),
                module.locale().raw("hoverDelete"),
                "/note " + user.getName() + " delete " + index
        );

        var operator = CoreModule.getUsername(note.createdBy);
        var dateFormatter = new SimpleDateFormat(CoreModule.getConfig().dateTimeFormat);
        var placeholders = Map.of(
                "operator", Component.nullToEmpty(operator),
                "date", Component.nullToEmpty(dateFormatter.format(note.creationDate)),
                "message", Format.parse(note.note),
                "deleteButton", deleteButton
        );

        context.getSource().sendSuccess(() -> module.locale().get("noteDetails", placeholders), false);

        return 1;
    }

    private int deleteNote(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var user = LocalGameProfile.getProfile(context, "user");
        var notes = module.getNotes(user.getId());
        var index = IntegerArgumentType.getInteger(context, "index");

        if (index < notes.size()) {
            notes.remove(index);
            context.getSource().sendSuccess(() -> module.locale().get("noteDeleted"), false);
        } else {
            context.getSource().sendSuccess(() -> module.locale().get("notFound"), false);
            return 0;
        }

        return 1;
    }

    private int addNote(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var user = LocalGameProfile.getProfile(context, "user");

        UUID operatorId = new UUID(0, 0);
        if (context.getSource().isPlayer())
            operatorId = context.getSource().getPlayer().getUUID();

        var message = StringArgumentType.getString(context, "message");

        var note = new Note(message, operatorId);
        var notes = module.getNotes(user.getId());

        notes.add(note);
        var index = notes.size() - 1;

        context.getSource().sendSuccess(() -> module.locale().get("noteAdded"), false);

        var checkButton = Components.button(
                module.locale().raw("checkButton"),
                module.locale().raw("hoverCheck"),
                "/notes " + user.getName() + " check " + index
        );
        final var text = module.locale().get("addedNotification", Map.of(
                "operator", context.getSource().getDisplayName(),
                "user", Component.nullToEmpty(user.getName()),
                "checkButton", checkButton
        ));

        context.getSource().getServer().getPlayerList().getPlayers().forEach(pl -> {
            if (Permissions.check(pl, getPermissionNode("notify"), 2)) {
                pl.sendSystemMessage(text);
            }
        });

        return 1;
    }

    private int clearNotes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var user = LocalGameProfile.getProfile(context, "user");

        var notes = module.getNotes(user.getId());
        notes.clear();

        context.getSource().sendSuccess(() -> module.locale().get("notesCleared", Map.of(
                "user", Component.nullToEmpty(user.getName())
        )), true);

        return 1;
    }
}
