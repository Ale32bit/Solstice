package me.alexdevs.solstice.modules.note;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleProperties;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.note.commands.NotesCommand;
import me.alexdevs.solstice.modules.note.data.Note;
import me.alexdevs.solstice.modules.note.data.NoteConfig;
import me.alexdevs.solstice.modules.note.data.NoteLocale;
import me.alexdevs.solstice.modules.note.data.NotePlayerData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;
public class NoteModule extends ModuleBase {
    

    public NoteModule(ModuleProperties properties) {
        super(properties);
    }

    @Override
    public void init() {
        registerConfig(NoteConfig.class, NoteConfig::new);
        registerLocale(NoteLocale.MODULE);
        registerPlayerData(NotePlayerData.class, NotePlayerData::new);

        commands.add(new NotesCommand(this));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var config = Solstice.configManager.getData(NoteConfig.class);
            if (!config.showLogin)
                return;

            var player = handler.getPlayer();
            var notes = getNotes(player.getUUID());

            if (notes.isEmpty())
                return;

            var context = PlaceholderContext.of(player);

            var checkButton = Components.button(
                    locale().raw("checkButton"),
                    locale().raw("hoverCheck"),
                    "/notes " + PlayerUtils.getName(player.getGameProfile())
            );
            final var text = locale().get("loginInfo", context, Map.of(
                    "user", Component.nullToEmpty(PlayerUtils.getName(player.getGameProfile())),
                    "notes", Component.nullToEmpty(String.valueOf(notes.size())),
                    "checkButton", checkButton
            ));

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
