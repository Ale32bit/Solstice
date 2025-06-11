package me.alexdevs.solstice.modules.note;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.proxy.ProxyServerPlayConnectionEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.permissions.Permissions;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.modules.note.commands.NotesCommand;
import me.alexdevs.solstice.modules.note.data.Note;
import me.alexdevs.solstice.modules.note.data.NoteConfig;
import me.alexdevs.solstice.modules.note.data.NoteLocale;
import me.alexdevs.solstice.modules.note.data.NotePlayerData;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NoteModule extends ModuleBase.Toggleable {
    public static final String ID = "note";

    public NoteModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, NoteConfig.class, NoteConfig::new);
        Solstice.localeManager.registerModule(ID, NoteLocale.MODULE);
        Solstice.playerData.registerData(ID, NotePlayerData.class, NotePlayerData::new);

        commands.add(new NotesCommand(this));

        ProxyServerPlayConnectionEvents.JOIN.register((player, server) -> {
            var config = Solstice.configManager.getData(NoteConfig.class);

            if (!config.showLogin)
                return;

            var notes = getNotes(player.getUUID());

            if (notes.isEmpty())
                return;

            var context = PlaceholderContext.of(player);

            var checkButton = Components.button(
                    locale().raw("checkButton"),
                    locale().raw("hoverCheck"),
                    "/notes " + player.getGameProfile().getName()
            );
            final var text = locale().get(
                    "loginInfo", context, Map.of(
                            "user", Component.nullToEmpty(player.getGameProfile().getName()),
                            "notes", Component.nullToEmpty(String.valueOf(notes.size())),
                            "checkButton", checkButton
                    )
            );

            Solstice.nextTick(() ->
                    server.getPlayerList().getPlayers().forEach(pl -> {
                        if (Permissions.check(pl, getPermissionNode("showonlogin"), 2)) {
                            pl.sendSystemMessage(text);
                        }
                    }));
        });
    }

    public NotePlayerData getData(UUID uuid) {
        return Solstice.playerData.get(uuid).getData(NotePlayerData.class);
    }

    public List<Note> getNotes(UUID uuid) {
        var data = getData(uuid);
        return data.notes;
    }
}
