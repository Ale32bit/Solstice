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
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AfkModule extends ModuleBase {
    public static final String ID = "afk";

    public static final double sprintSpeed = 0.280617;
    public static final double walkSpeed = 0.215859;
    public static final double sneakSpeed = 0.0841;

    public static final int LEADERBOARD_SIZE = 10;

    private final Map<UUID, PlayerActivityState> activities = new ConcurrentHashMap<>();

    public AfkModule() {
        super(ID);

        Solstice.configManager.registerData(ID, AfkConfig.class, AfkConfig::new);
        Solstice.localeManager.registerModule(ID, AfkLocale.MODULE);
        Solstice.playerData.registerData(ID, AfkPlayerData.class, AfkPlayerData::new);
        Solstice.serverData.registerData(ID, AfkServerData.class, AfkServerData::new);

        this.commands.add(new AfkCommand(this));
        this.commands.add(new ActiveTimeCommand(this));

        Placeholders.register(Identifier.of(Solstice.MOD_ID, "afk"), (context, arg) -> {
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
            activities.put(handler.getPlayer().getUuid(), new PlayerActivityState(handler.getPlayer(), server.getTicks()));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            activities.remove(handler.getPlayer().getUuid());
        });

        ServerTickEvents.END_SERVER_TICK.register(this::tick);

        PlayerActivityEvents.AFK.register((player, server) -> {
            var config = getConfig();

            Solstice.LOGGER.info("{} is AFK. Active time: {} seconds.", player.getGameProfile().getName(), getActiveTime(player.getUuid()));
            if (!config.announce)
                return;

            var playerContext = PlaceholderContext.of(player);

            Solstice.getInstance().broadcast(locale().get("goneAfk", playerContext));
        });

        PlayerActivityEvents.AFK_RETURN.register((player, server) -> {
            var config = getConfig();
            Solstice.LOGGER.info("{} is no longer AFK. Active time: {} seconds.", player.getGameProfile().getName(), getActiveTime(player.getUuid()));
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
        var activePlayers = Solstice.server.getPlayerManager().getPlayerList()
                .stream().filter(player -> !isPlayerAfk(player));

        activePlayers.forEach(player -> {
            var playerData = getPlayerData(player.getUuid());
            playerData.activeTime++;
            tryInsertLeaderboard(player, playerData.activeTime);
        });
    }

    private void tryInsertLeaderboard(ServerPlayerEntity player, int activeTime) {
        var serverData = getServerData();
        var leaderboard = serverData.leaderboard;

        // if in list, update
        var entry = leaderboard.stream().filter(e -> e.uuid().equals(player.getUuid())).findFirst();
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
                leaderboard.add(new LeaderboardEntry(player.getGameProfile().getName(), player.getUuid(), activeTime));
                leaderboard.sort((o1, o2) -> Integer.compare(o2.activeTime(), o1.activeTime()));
            }
        } else {
            leaderboard.add(new LeaderboardEntry(player.getGameProfile().getName(), player.getUuid(), activeTime));
        }
    }

    private void tick(MinecraftServer server) {
        var config = getConfig();
        if (!config.enable)
            return;

        server.getPlayerManager().getPlayerList().forEach(player -> {
            var activity = activities.computeIfAbsent(player.getUuid(), uuid -> new PlayerActivityState(player, server.getTicks()));

            var curLocation = new ServerLocation(player);
            var oldLocation = activity.location;
            activity.location = curLocation;

            var delta = curLocation.getDelta(oldLocation);
            var horizontalDelta = new Vec3d(delta.getX(), 0, delta.getZ());

            var speed = horizontalDelta.length();

            // Suppose the player in a vehicle will look around, so we only check for movement when not in a vehicle.
            if (player.getVehicle() == null) {
                // Defeats some anti-afk stuff, like pools. Works best when no lag.
                if ((player.isSneaking() && speed >= sneakSpeed) || (player.isSprinting() && speed >= sprintSpeed) || (speed >= walkSpeed)) {
                    clearAfk(player);
                }
            }

            // Looking around requires player input
            if (curLocation.getPitch() != oldLocation.getPitch() || curLocation.getYaw() != oldLocation.getYaw()) {
                clearAfk(player);
            }

            var ticks = server.getTicks();
            if (activity.lastUpdate < ticks - config.timeTrigger * 20) {
                if (!activity.isAfk) {
                    activity.isAfk = true;
                    PlayerActivityEvents.AFK.invoker().onAfk(player, server);
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

    public boolean isPlayerAfk(ServerPlayerEntity player) {
        return activities.containsKey(player.getUuid()) && activities.get(player.getUuid()).isAfk;
    }

    public void setPlayerAfk(ServerPlayerEntity player, boolean isAfk) {
        if (!activities.containsKey(player.getUuid()))
            return;

        var config = getConfig();
        var activity = activities.get(player.getUuid());
        if (isAfk) {
            activity.lastUpdate = activity.lastUpdate - (config.timeTrigger * 20);
        } else {
            clearAfk(player);
        }
    }

    public int getActiveTime(UUID playerUuid) {
        return getPlayerData(playerUuid).activeTime;
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

        var onlinePlayers = Solstice.server.getPlayerManager().getPlayerList().stream().map(Entity::getUuid).toList();
        Solstice.playerData.disposeMissing(onlinePlayers);
    }

    public List<LeaderboardEntry> getActiveTimeLeaderboard() {
        var serverData = getServerData();
        return Collections.unmodifiableList(serverData.leaderboard);
    }

    private void clearAfk(ServerPlayerEntity player) {
        if (!activities.containsKey(player.getUuid()))
            return;

        var activity = activities.get(player.getUuid());
        activity.lastUpdate = Solstice.server.getTicks();

        if (!activity.afkEnabled)
            return;

        if (activity.isAfk) {
            activity.isAfk = false;
            PlayerActivityEvents.AFK_RETURN.invoker().onAfkReturn(player, Solstice.server);
        }

    }

    private void registerTriggers() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            clearAfk((ServerPlayerEntity) player);
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            clearAfk((ServerPlayerEntity) player);
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            clearAfk((ServerPlayerEntity) player);
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            clearAfk((ServerPlayerEntity) player);
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            clearAfk((ServerPlayerEntity) player);
            return TypedActionResult.pass(player.getStackInHand(hand));
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            clearAfk(sender);
            return true;
        });

        CommandEvents.ALLOW_COMMAND.register((source, command) -> {
            if (!source.isExecutedByPlayer())
                return true;

            clearAfk(source.getPlayer());
            return true;
        });
    }
}
