package me.alexdevs.solstice.modules.afk;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.events.PlayerActivityEvents;
import me.alexdevs.solstice.api.events.SolsticeEvents;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Format;
import me.alexdevs.solstice.modules.afk.commands.ActiveTimeCommand;
import me.alexdevs.solstice.modules.afk.commands.AfkCommand;
import me.alexdevs.solstice.modules.afk.data.AfkConfig;
import me.alexdevs.solstice.modules.afk.data.AfkLocale;
import me.alexdevs.solstice.modules.afk.data.AfkPlayerData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AfkModule extends ModuleBase {
    public static final String ID = "afk";

    private final Map<UUID, PlayerActivityState> activities = new ConcurrentHashMap<>();

    public AfkModule() {
        super(ID);

        Solstice.configManager.registerData(ID, AfkConfig.class, AfkConfig::new);
        Solstice.playerData.registerData(ID, AfkPlayerData.class, AfkPlayerData::new);
        Solstice.localeManager.registerModule(ID, AfkLocale.MODULE);

        this.commands.add(new AfkCommand(this));
        this.commands.add(new ActiveTimeCommand(this));

        Placeholders.register(new Identifier(Solstice.MOD_ID, "afk"), (context, arg) -> {
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
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            activities.put(handler.getPlayer().getUuid(), new PlayerActivityState(handler.getPlayer(), server.getTicks()));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            activities.remove(handler.getPlayer().getUuid());
        });

        ServerTickEvents.END_SERVER_TICK.register(this::tick);

        registerTriggers();
    }

    private void updateActiveTime() {
        var activePlayers = Solstice.server.getPlayerManager().getPlayerList()
                .stream().filter(player -> !isPlayerAfk(player));

        activePlayers.forEach(player -> {
            getPlayerData(player.getUuid()).activeTime++;
        });
    }

    private void tick(MinecraftServer server) {
        var currentTick = server.getTicks();
        var config = getConfig();
        server.getPlayerManager().getPlayerList().forEach(player -> {
            var activity = activities.get(player.getUuid());

            var curLocation = new ServerLocation(player);


            if(activity.lastUpdate > config.timeTrigger * 20) {
                if(!activity.isAfk) {
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
        return activities.get(player.getUuid()) != null && activities.get(player.getUuid()).isAfk;
    }

    public void setPlayerAfk(ServerPlayerEntity player, boolean isAfk) {
        if (!activities.containsKey(player.getUuid()))
            return;

        var activity = activities.get(player.getUuid());
        activity.isAfk = isAfk;
    }

    public int getActiveTime(UUID playerUuid) {
        return getPlayerData(playerUuid).activeTime;
    }

    private void clearAfk(ServerPlayerEntity player) {
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

    }
}
