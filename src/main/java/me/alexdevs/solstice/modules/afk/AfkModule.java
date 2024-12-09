package me.alexdevs.solstice.modules.afk;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.PlayerActivityEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.core.AfkTracker;
import me.alexdevs.solstice.util.Format;
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
    private int absentTimeTrigger = Solstice.config().afk.afkTimeTrigger * 20; // seconds * 20 ticks

    private final ConcurrentHashMap<UUID, AfkTracker.PlayerActivityState> playerActivityStates = new ConcurrentHashMap<>();

    public static Text afkTag;

    public AfkModule() {
        // TODO: use new locale, player data and config
        loadAfkTag();

        Placeholders.register(new Identifier(Solstice.MOD_ID, "afk"), (context, argument) -> {
            if(!context.hasPlayer())
                return PlaceholderResult.invalid("No player!");
            var player = context.player();
            if(isPlayerAfk(player.getUuid())) {
                return PlaceholderResult.value(afkTag);
            } else {
                return PlaceholderResult.value("");
            }
        });

        if(!Solstice.config().afk.enableAfk)
            return;

        PlayerActivityEvents.AFK.register((player, server) -> {
            Solstice.LOGGER.info("{} is AFK. Active time: {} seconds.", player.getGameProfile().getName(), getActiveTime(player));
            if(!Solstice.config().afk.announceAfk)
                return;

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(Format.parse(
                    Solstice.locale().commands.afk.goneAfk,
                    playerContext
            ));
        });

        PlayerActivityEvents.AFK_RETURN.register((player, server) -> {
            Solstice.LOGGER.info("{} is no longer AFK. Active time: {} seconds.", player.getGameProfile().getName(), getActiveTime(player));
            if(!Solstice.config().afk.announceAfk)
                return;

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(Format.parse(
                    Solstice.locale().commands.afk.returnAfk,
                    playerContext
            ));
        });

        SolsticeEvents.RELOAD.register(inst -> loadAfkTag());

        ServerTickEvents.END_SERVER_TICK.register(this::updatePlayers);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            final var player = handler.getPlayer();
            playerActivityStates.put(player.getUuid(), new AfkTracker.PlayerActivityState(player, server.getTicks()));
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

    private static void loadAfkTag() {
        afkTag = Format.parse(Solstice.locale().commands.afk.tag);
    }

    private void updatePlayer(ServerPlayerEntity player, MinecraftServer server) {
        var currentTick = server.getTicks();
        var playerState = playerActivityStates.computeIfAbsent(player.getUuid(), uuid -> new AfkTracker.PlayerActivityState(player, currentTick));

        var oldPosition = playerState.position;
        var newPosition = new AfkTracker.PlayerPosition(player);
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
            var playerState = Solstice.state.getPlayerState(player);
            var interval = currentTick - playerActivityState.activeStart;
            playerState.activeTime += interval / 20;
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

    public static class PlayerPosition {
        public String dimension;
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;


        public boolean equals(AfkTracker.PlayerPosition obj) {
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
        public AfkTracker.PlayerPosition position;
        public int lastUpdate;
        public boolean isAfk;
        public int activeStart;

        public PlayerActivityState(ServerPlayerEntity player, int lastUpdate) {
            this.position = new AfkTracker.PlayerPosition(player);
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

        if (afk) {
            playerActivityStates.get(player.getUuid()).lastUpdate = -absentTimeTrigger - 20; // just to be sure
        } else {
            resetAfkState(player, Solstice.server);
        }

        updatePlayer(player, Solstice.server);
    }

    public int getActiveTime(ServerPlayerEntity player) {
        var playerState = Solstice.state.getPlayerState(player);
        return playerState.activeTime;
    }
}
