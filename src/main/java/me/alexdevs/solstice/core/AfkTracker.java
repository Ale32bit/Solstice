package me.alexdevs.solstice.core;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.alexdevs.solstice.api.events.PlayerActivityEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
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

public class AfkTracker {
    private static final int cycleDelay = 1;
    private static final int absentTimeTrigger = Solstice.config().afk.afkTimeTrigger * 20; // seconds * 20 ticks

    private final ConcurrentHashMap<UUID, PlayerActivityState> playerActivityStates = new ConcurrentHashMap<>();

    private static final AfkTracker instance = new AfkTracker();

    public static AfkTracker getInstance() {
        return instance;
    }

    public static Text afkTag;

    public static void register() {
        loadAfkTag();

        Placeholders.register(new Identifier(Solstice.MOD_ID, "afk"), (context, argument) -> {
            if(!context.hasPlayer())
                return PlaceholderResult.invalid("No player!");
            var player = context.player();
            if(instance.isPlayerAfk(player.getUuid())) {
                return PlaceholderResult.value(afkTag);
            } else {
                return PlaceholderResult.value("");
            }
        });

        PlayerActivityEvents.AFK.register((player, server) -> {
            Solstice.LOGGER.info("{} is AFK. Active time: {} seconds.", player.getGameProfile().getName(), getInstance().getActiveTime(player));

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(Format.parse(
                    Solstice.locale().commands.afk.goneAfk,
                    playerContext
            ));
        });

        PlayerActivityEvents.AFK_RETURN.register((player, server) -> {
            Solstice.LOGGER.info("{} is no longer AFK. Active time: {} seconds.", player.getGameProfile().getName(), getInstance().getActiveTime(player));

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(Format.parse(
                    Solstice.locale().commands.afk.returnAfk,
                    playerContext
            ));
        });

        SolsticeEvents.RELOAD.register(inst -> loadAfkTag());
    }

    private static void loadAfkTag() {
        afkTag = Format.parse(Solstice.locale().commands.afk.tag);
    }

    private AfkTracker() {
        ServerTickEvents.END_SERVER_TICK.register(this::updatePlayers);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            final var player = handler.getPlayer();
            playerActivityStates.put(player.getUuid(), new PlayerActivityState(player, server.getTicks()));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            updatePlayerActiveTime(handler.getPlayer(), server.getTicks());
            playerActivityStates.remove(handler.getPlayer().getUuid());

            // sync to LP
            //var activeTime = String.valueOf(getActiveTime(handler.getPlayer()));
            //var playerData = PlayerData.getPlayer(handler.getPlayer());

            //playerData.set(PlayerData.KEYS.activeTime, activeTime).join();
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
            var playerState = Solstice.state.getPlayerState(player.getUuid());
            var interval = currentTick - playerActivityState.activeStart;
            playerState.activeTime += interval / 20;
        }
    }

    private void updatePlayers(MinecraftServer server) {
        var players = server.getPlayerManager().getPlayerList();
        players.forEach(player -> {
            updatePlayer(player, server);
        });
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

    public static class PlayerPosition {
        public String dimension;
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;


        public boolean equals(PlayerPosition obj) {
            return x == obj.x && y == obj.y && z == obj.z
                    && yaw == obj.yaw && pitch == obj.pitch
                    && dimension.equals(obj.dimension);
        }

        public PlayerPosition(ServerPlayerEntity player) {
            dimension = player.getWorld().getRegistryKey().getValue().toString();
            x = player.getX();
            y = player.getY();
            z = player.getZ();
            yaw = player.getYaw();
            pitch = player.getPitch();
        }
    }

    public static class PlayerActivityState {
        public PlayerPosition position;
        public int lastUpdate;
        public boolean isAfk;
        public int activeStart;

        public PlayerActivityState(ServerPlayerEntity player, int lastUpdate) {
            this.position = new PlayerPosition(player);
            this.lastUpdate = lastUpdate;
            this.isAfk = false;
            this.activeStart = lastUpdate;
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

        var server = player.getWorld().getServer();

        if (afk) {
            playerActivityStates.get(player.getUuid()).lastUpdate = -absentTimeTrigger - 20; // just to be sure
        } else {
            resetAfkState(player, server);
        }

        updatePlayer(player, server);
    }

    public int getActiveTime(ServerPlayerEntity player) {
        var playerState = Solstice.state.getPlayerState(player.getUuid());
        return playerState.activeTime;
    }
}
