package me.alexdevs.solstice.modules.afk;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.CommandEvents;
import me.alexdevs.solstice.api.events.PlayerActivityEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.afk.commands.ActiveTimeCommand;
import me.alexdevs.solstice.modules.afk.commands.AfkCommand;
import me.alexdevs.solstice.modules.afk.data.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AfkModule extends ModuleBase.Toggleable {
    public static final double sprintSpeed = 0.280617;
    public static final double walkSpeed = 0.215859;
    public static final double sneakSpeed = 0.0841;

    public static final int LEADERBOARD_SIZE = 10;

    public AfkModule(ResourceLocation id) {
        super(id);
    }

    public enum AfkTriggerReason {
        MANUAL,
        MOVEMENT,
        LOOK_CHANGE,
        CHAT_MESSAGE,
        COMMAND,
        BLOCK_ATTACK,
        BLOCK_INTERACT,
        ENTITY_ATTACK,
        ENTITY_INTERACT,
        ITEM_USE,
    }

    private final Map<UUID, PlayerActivityState> activities = new ConcurrentHashMap<>();


    @Override
    public void init() {
        registerConfig(AfkConfig.class, AfkConfig::new);
        registerLocale(AfkLocale.MODULE);
        registerPlayerData(AfkPlayerData.class, AfkPlayerData::new);
        registerServerData(AfkServerData.class, AfkServerData::new);

        this.commands.add(new AfkCommand(this));
        this.commands.add(new ActiveTimeCommand(this));

        Placeholders.register(ResourceLocation.fromNamespaceAndPath(Solstice.MOD_ID, "afk"), (context, arg) -> {
            if (!context.hasPlayer())
                return PlaceholderResult.invalid("No player!");

            var player = context.player();

            if (isPlayerAfk(player))
                return PlaceholderResult.value(Format.parse(getConfig().tag));
            else
                return PlaceholderResult.value("");
        });

        SolsticeEvents.READY.register((instance, server) -> {
            Solstice.scheduler.scheduleAtFixedRate(this::updateActiveTime, 0, 1, TimeUnit.SECONDS);
            calculateLeaderboard();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            activities.put(handler.getPlayer().getUUID(), new PlayerActivityState(handler.getPlayer(), server.getTickCount()));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            activities.remove(handler.getPlayer().getUUID());
        });

        ServerTickEvents.END_SERVER_TICK.register(this::tick);

        PlayerActivityEvents.AFK.register((player) -> {
            var config = getConfig();

            if (player.serverLevel().canSleepThroughNights()) {
                player.serverLevel().updateSleepingPlayerList();
            }

            Solstice.LOGGER.info("{} is AFK. Active time: {} seconds.", player.getGameProfile().getName(), getActiveTime(player.getUUID()));
            if (!config.announce)
                return;

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(locale().get("goneAfk", playerContext));
        });

        PlayerActivityEvents.AFK_RETURN.register((player, reason) -> {
            var config = getConfig();

            if (player.serverLevel().canSleepThroughNights()) {
                player.serverLevel().updateSleepingPlayerList();
            }

            Solstice.LOGGER.info("{} is no longer AFK due to {}. Active time: {} seconds.", player.getGameProfile().getName(), reason.name(), getActiveTime(player.getUUID()));
            if (!config.announce)
                return;

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(locale().get("returnAfk", playerContext));
        });

        registerTriggers();
    }

    public AfkServerData getServerData() {
        return Solstice.serverData.getData(AfkServerData.class);
    }

    private void updateActiveTime() {
        var activePlayers = Solstice.server.getPlayerList().getPlayers()
                .stream().filter(player -> !isPlayerAfk(player));

        activePlayers.forEach(player -> {
            var activity = activities.computeIfAbsent(player.getUUID(), uuid -> new PlayerActivityState(player, player.getServer().getTickCount()));
            if (!activity.activeTimeEnabled)
                return;

            var playerData = getPlayerData(player.getUUID());
            playerData.activeTime++;
            tryInsertLeaderboard(player, playerData.activeTime);
        });
    }

    private void tryInsertLeaderboard(ServerPlayer player, int activeTime) {
        var serverData = getServerData();
        var leaderboard = serverData.leaderboard;

        // if in list, update
        var entry = leaderboard.stream().filter(e -> e.uuid().equals(player.getUUID())).findFirst();
        if (entry.isPresent()) {
            entry.get().activeTime(activeTime);
            entry.get().name(player.getGameProfile().getName());
            leaderboard.sort((o1, o2) -> Integer.compare(o2.activeTime(), o1.activeTime()));
            return;
        }

        // if not in list, insert
        var smallest = leaderboard.stream().min(Comparator.comparingInt(LeaderboardEntry::activeTime));
        if (smallest.isPresent()) {
            if (smallest.get().activeTime() < activeTime) {
                leaderboard.remove(smallest.get());
                leaderboard.add(new LeaderboardEntry(player.getGameProfile().getName(), player.getUUID(), activeTime));
                leaderboard.sort((o1, o2) -> Integer.compare(o2.activeTime(), o1.activeTime()));
            }
        } else {
            leaderboard.add(new LeaderboardEntry(player.getGameProfile().getName(), player.getUUID(), activeTime));
        }
    }

    private void tick(MinecraftServer server) {
        var config = getConfig();
        if (!config.enable)
            return;

        server.getPlayerList().getPlayers().forEach(player -> {
            var activity = activities.computeIfAbsent(player.getUUID(), uuid -> new PlayerActivityState(player, server.getTickCount()));

            var curLocation = new ServerLocation(player);
            var oldLocation = activity.location;
            activity.location = curLocation;

            var delta = curLocation.getDelta(oldLocation);
            var horizontalDelta = new Vec3(delta.x(), 0, delta.z());

            var speed = horizontalDelta.length();

            // Suppose the player in a vehicle will look around, so we only check for movement when not in a vehicle.
            if (player.getVehicle() == null) {
                // Defeats some anti-afk stuff, like pools. Works best when no lag.
                if ((player.isShiftKeyDown() && speed >= sneakSpeed) || (player.isSprinting() && speed >= sprintSpeed) || (speed >= walkSpeed)) {
                    if (getConfig().triggers.onMovement) {
                        clearAfk(player, AfkTriggerReason.MOVEMENT);
                    }
                }
            }

            // Looking around requires player input
            if (curLocation.getPitch() != oldLocation.getPitch() || curLocation.getYaw() != oldLocation.getYaw()) {
                if (getConfig().triggers.onLookChange) {
                    clearAfk(player, AfkTriggerReason.LOOK_CHANGE);
                }
            }

            var ticks = server.getTickCount();
            if (activity.lastUpdate < ticks - config.timeTrigger * 20) {
                if (!activity.isAfk && activity.afkEnabled) {
                    activity.isAfk = true;
                    PlayerActivityEvents.AFK.invoker().onAfk(player);
                }
            }
        });
    }

    public AfkConfig getConfig() {
        return Solstice.configManager.getData(AfkConfig.class);
    }

    public AfkPlayerData getPlayerData(UUID playerUuid) {
        return Solstice.playerData.get(playerUuid).getData(AfkPlayerData.class);
    }

    public boolean isPlayerAfk(ServerPlayer player) {
        return activities.containsKey(player.getUUID()) && activities.get(player.getUUID()).isAfk;
    }

    public void setPlayerAfk(ServerPlayer player, boolean isAfk) {
        if (!activities.containsKey(player.getUUID()))
            return;

        var config = getConfig();
        var activity = activities.get(player.getUUID());
        if (isAfk) {
            activity.lastUpdate = activity.lastUpdate - (config.timeTrigger * 20);
        } else {
            clearAfk(player, AfkTriggerReason.MANUAL);
        }
    }

    public int getActiveTime(UUID playerUuid) {
        return getPlayerData(playerUuid).activeTime;
    }

    public void forceRecalculateLeaderboard() {
        getServerData().forceCalculateLeaderboard = true;
        calculateLeaderboard();
    }

    private void calculateLeaderboard() {
        var serverData = getServerData();
        if (!serverData.forceCalculateLeaderboard)
            return;

        serverData.forceCalculateLeaderboard = false;

        var userCache = Solstice.getUserCache();
        var temp = new ArrayList<LeaderboardEntry>();
        for (var name : userCache.getAllNames()) {
            var profile = userCache.getByName(name);
            if (profile.isEmpty())
                continue;

            var playerData = Solstice.playerData.get(profile.get().getId()).getData(AfkPlayerData.class);
            if (playerData.activeTime > 0) {
                var entry = new LeaderboardEntry(profile.get().getName(), profile.get().getId(), playerData.activeTime);
                temp.add(entry);
            }
        }

        temp.sort((o1, o2) -> Integer.compare(o2.activeTime(), o1.activeTime()));

        serverData.leaderboard.clear();

        for (var i = 0; i < Math.min(temp.size(), LEADERBOARD_SIZE); i++) {
            serverData.leaderboard.add(temp.get(i));
        }

        var onlinePlayers = Solstice.server.getPlayerList().getPlayers().stream().map(Entity::getUUID).toList();
        Solstice.playerData.disposeMissing(onlinePlayers);
    }

    public List<LeaderboardEntry> getActiveTimeLeaderboard() {
        var serverData = getServerData();
        return Collections.unmodifiableList(serverData.leaderboard);
    }

    public List<ServerPlayer> getCurrentActivePlayers() {
        return Solstice.server.getPlayerList().getPlayers().stream().filter(player -> !isPlayerAfk(player)).toList();
    }

    private void clearAfk(ServerPlayer player, AfkTriggerReason reason) {
        if (!activities.containsKey(player.getUUID()))
            return;

        var activity = activities.get(player.getUUID());
        activity.lastUpdate = Solstice.server.getTickCount();

        if (!activity.afkEnabled)
            return;

        if (activity.isAfk) {
            activity.isAfk = false;
            PlayerActivityEvents.AFK_RETURN.invoker().onAfkReturn(player, reason);
        }

    }

    private void registerTriggers() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (getConfig().triggers.onBlockAttack) {
                clearAfk((ServerPlayer) player, AfkTriggerReason.BLOCK_ATTACK);
            }
            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (getConfig().triggers.onEntityAttack) {
                clearAfk((ServerPlayer) player, AfkTriggerReason.ENTITY_ATTACK);
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (getConfig().triggers.onBlockInteract) {
                clearAfk((ServerPlayer) player, AfkTriggerReason.BLOCK_INTERACT);
            }
            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (getConfig().triggers.onEntityInteract) {
                clearAfk((ServerPlayer) player, AfkTriggerReason.ENTITY_INTERACT);
            }
            return InteractionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (getConfig().triggers.onItemUse) {
                clearAfk((ServerPlayer) player, AfkTriggerReason.ITEM_USE);
            }
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            if (getConfig().triggers.onChat) {
                clearAfk(sender, AfkTriggerReason.CHAT_MESSAGE);
            }
            return true;
        });

        CommandEvents.ALLOW_COMMAND.register((source, command) -> {
            if (!source.isPlayer())
                return true;

            if (getConfig().triggers.onCommand) {
                clearAfk(source.getPlayer(), AfkTriggerReason.COMMAND);
            }
            return true;
        });
    }
}
