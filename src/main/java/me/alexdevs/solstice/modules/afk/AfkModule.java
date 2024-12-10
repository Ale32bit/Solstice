package me.alexdevs.solstice.modules.afk;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.PlayerActivityEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.locale.Locale;
import me.alexdevs.solstice.modules.afk.commands.AfkCommand;
import me.alexdevs.solstice.modules.afk.data.AfkConfig;
import me.alexdevs.solstice.modules.afk.data.AfkLocale;
import me.alexdevs.solstice.modules.afk.data.AfkPlayerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AfkModule {
    public static final String ID = "afk";
    private final ConcurrentHashMap<UUID, PlayerActivityState> playerActivityStates = new ConcurrentHashMap<>();

    public static Text afkTag;
    private int absentTimeTrigger;

    private Locale locale;
    private AfkConfig config;

    public AfkModule() {
        Solstice.newConfigManager.registerData(ID, AfkConfig.class, AfkConfig::new);
        Solstice.playerData.registerData(ID, AfkPlayerData.class, AfkPlayerData::new);
        Solstice.newLocaleManager.registerModule(ID, AfkLocale.MODULE);

        CommandRegistrationCallback.EVENT.register(AfkCommand::new);


        SolsticeEvents.READY.register((instance, server) -> register());
    }

    private void load() {
        loadConfig();
    }

    private void register() {
        locale = Solstice.newLocaleManager.getLocale(ID);
        config = Solstice.newConfigManager.getData(AfkConfig.class);

        load();

        Placeholders.register(new Identifier(Solstice.MOD_ID, "afk"), (context, argument) -> {
            if (!context.hasPlayer())
                return PlaceholderResult.invalid("No player!");
            var player = context.player();
            if (isPlayerAfk(player.getUuid())) {
                return PlaceholderResult.value(afkTag);
            } else {
                return PlaceholderResult.value("");
            }
        });

        if (!config.enable)
            return;

        PlayerActivityEvents.AFK.register((player, server) -> {
            Solstice.LOGGER.info("{} is AFK. Active time: {} seconds.", player.getGameProfile().getName(), getActiveTime(player));
            if (!config.announce)
                return;

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(locale.get("goneAfk", playerContext));
        });

        PlayerActivityEvents.AFK_RETURN.register((player, server) -> {
            Solstice.LOGGER.info("{} is no longer AFK. Active time: {} seconds.", player.getGameProfile().getName(), getActiveTime(player));
            if (!config.announce)
                return;

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(locale.get("returnAfk", playerContext));
        });

        SolsticeEvents.RELOAD.register(inst -> load());

        ServerTickEvents.END_SERVER_TICK.register(this::updatePlayers);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            final var player = handler.getPlayer();
            playerActivityStates.put(player.getUuid(), new PlayerActivityState(player, server.getTicks()));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            updatePlayerActiveTime(handler.getPlayer(), server.getTicks());
            playerActivityStates.remove(handler.getPlayer().getUuid());
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            resetAfkState((ServerPlayerEntity) player, world.getServer());
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            resetAfkState((ServerPlayerEntity) player, world.getServer());
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            resetAfkState((ServerPlayerEntity) player, world.getServer());
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            resetAfkState((ServerPlayerEntity) player, world.getServer());
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            resetAfkState((ServerPlayerEntity) player, world.getServer());
            return TypedActionResult.pass(player.getStackInHand(hand));
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            resetAfkState(sender, sender.getServer());
            return true;
        });

        ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register((message, source, params) -> {
            if (!source.isExecutedByPlayer())
                return true;
            resetAfkState(source.getPlayer(), source.getServer());
            return true;
        });
    }

    private void loadConfig() {
        afkTag = locale.get("tag");
        absentTimeTrigger = config.timeTrigger * 20;
    }

    private void updatePlayer(ServerPlayerEntity player, MinecraftServer server) {
        var currentTick = server.getTicks();
        var playerState = playerActivityStates.computeIfAbsent(player.getUuid(), uuid -> new PlayerActivityState(player, currentTick));

        var oldPosition = playerState.position;
        var newPosition = new PlayerPosition(player);
        if (!oldPosition.equals(newPosition)) {
            playerState.position = newPosition;
            resetAfkState(player, server);
            return;
        }

        if (playerState.isAfk)
            return;

        if ((playerState.lastUpdate + absentTimeTrigger) <= currentTick) {
            // player is afk after 5 mins
            updatePlayerActiveTime(player, currentTick);
            playerState.isAfk = true;
            PlayerActivityEvents.AFK.invoker().onAfk(player, server);
        }
    }

    private void updatePlayerActiveTime(ServerPlayerEntity player, int currentTick) {
        var playerActivityState = playerActivityStates.get(player.getUuid());
        if (!playerActivityState.isAfk) {
            var data = Solstice.playerData.get(player).getData(AfkPlayerData.class);
            var interval = currentTick - playerActivityState.activeStart;
            data.activeTime += interval / 20;
        }
    }

    private void updatePlayers(MinecraftServer server) {
        var players = server.getPlayerManager().getPlayerList();
        players.forEach(player -> updatePlayer(player, server));
    }

    private void resetAfkState(ServerPlayerEntity player, MinecraftServer server) {
        if (!playerActivityStates.containsKey(player.getUuid()))
            return;

        var playerState = playerActivityStates.get(player.getUuid());
        playerState.lastUpdate = server.getTicks();
        if (playerState.isAfk) {
            playerState.isAfk = false;
            playerState.activeStart = server.getTicks();
            PlayerActivityEvents.AFK_RETURN.invoker().onAfkReturn(player, server);
        }
    }

    public boolean isPlayerAfk(UUID playerUuid) {
        if (!playerActivityStates.containsKey(playerUuid)) {
            return false;
        }
        return playerActivityStates.get(playerUuid).isAfk;
    }

    public void setPlayerAfk(ServerPlayerEntity player, boolean afk) {
        if (!playerActivityStates.containsKey(player.getUuid())) {
            return;
        }

        if (afk) {
            playerActivityStates.get(player.getUuid()).lastUpdate = -absentTimeTrigger - 20; // just to be sure
        } else {
            resetAfkState(player, Solstice.server);
        }

        updatePlayer(player, Solstice.server);
    }

    public int getActiveTime(ServerPlayerEntity player) {
        var data = Solstice.playerData.get(player).getData(AfkPlayerData.class);
        return data.activeTime;
    }
}
