package me.alexdevs.solstice.modules.teleportRequest;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.text.Components;
import me.alexdevs.solstice.modules.notifications.NotificationsModule;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportAcceptCommand;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportAskCommand;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportAskHereCommand;
import me.alexdevs.solstice.modules.teleportRequest.commands.TeleportDenyCommand;
import me.alexdevs.solstice.modules.teleportRequest.data.Request;
import me.alexdevs.solstice.modules.teleportRequest.data.TeleportConfig;
import me.alexdevs.solstice.modules.teleportRequest.data.TeleportLocale;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class TeleportRequestModule extends ModuleBase.Toggleable {
    public static final String ID = "teleportrequest";

    private final Map<UUID, ConcurrentLinkedDeque<Request>> requests = new ConcurrentHashMap<>();

    public TeleportRequestModule() {
        super(ID);
    }

    @Override
    public void init() {
        Solstice.configManager.registerData(ID, TeleportConfig.class, TeleportConfig::new);
        Solstice.localeManager.registerModule(ID, TeleportLocale.MODULE);

        commands.add(new TeleportAcceptCommand(this));
        commands.add(new TeleportAskCommand(this));
        commands.add(new TeleportAskHereCommand(this));
        commands.add(new TeleportDenyCommand(this));

        Solstice.scheduler.scheduleAtFixedRate(this::tickDown, 0, 1, TimeUnit.SECONDS);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> requests.put(handler.getPlayer().getUuid(), new ConcurrentLinkedDeque<>()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> Solstice.nextTick(() -> requests.remove(handler.getPlayer().getUuid())));
    }

    private void tickDown() {
        for (var entry : requests.entrySet()) {
            entry.getValue().removeIf(Request::tickDown);
        }
    }

    public TeleportConfig getConfig() {
        return Solstice.configManager.getData(TeleportConfig.class);
    }

    public Request getRequestFromSource(ServerPlayerEntity player, ServerPlayerEntity source) {
        return requests.get(player.getUuid())
                .stream()
                .filter(r -> r.getSource().getUuid().equals(source.getUuid()))
                .findFirst()
                .orElse(null);
    }

    public Request getLatestRequest(ServerPlayerEntity player) {
        var reqs = requests.get(player.getUuid());
        if (reqs.isEmpty())
            return null;

        return reqs.getLast();
    }

    public void acceptRequest(ServerPlayerEntity player, Request request) {
        requests.get(player.getUuid()).remove(request);
        var source = request.getSource();
        var direction = request.getDirection();

        var map = Map.of(
                "player", player.getDisplayName()
        );

        player.sendMessage(locale().get("targetAccepted"));
        source.sendMessage(locale().get("sourceAccepted", map));

        if (direction == Request.Direction.SOURCE_TO_TARGET) {
            var location = new ServerLocation(player);
            location.teleport(source);
        } else {
            var location = new ServerLocation(source);
            location.teleport(player);
        }
    }

    public void refuseRequest(ServerPlayerEntity player, Request request) {
        requests.get(player.getUuid()).remove(request);
        var source = request.getSource();

        var map = Map.of(
                "player", player.getDisplayName()
        );

        player.sendMessage(locale().get("targetRefused"));
        source.sendMessage(locale().get("sourceRefused", map));
    }

    public void requestTo(ServerPlayerEntity source, ServerPlayerEntity target) {
        var request = new Request(source, getConfig().teleportRequestTimeout, Request.Direction.SOURCE_TO_TARGET);
        requests.computeIfAbsent(target.getUuid(), uuid -> new ConcurrentLinkedDeque<>()).add(request);

        var sourceContext = PlaceholderContext.of(source);
        var targetContext = PlaceholderContext.of(target);

        var placeholders = Map.of(
                "requesterPlayer", source.getDisplayName(),
                "acceptButton", Components.button(
                        locale().raw("~accept"),
                        locale().raw("~accept.hover"),
                        "/tpaccept " + source.getGameProfile().getName()),
                "refuseButton", Components.button(
                        locale().raw("~refuse"),
                        locale().raw("~refuse.hover"),
                        "/tpdeny " + source.getGameProfile().getName())
        );

        target.sendMessage(locale().get(
                "pendingTeleport",
                targetContext,
                placeholders
        ));

        source.sendMessage(locale().get(
                "requestSent",
                sourceContext
        ));

        NotificationsModule.notify(target);
    }

    public void requestToHere(ServerPlayerEntity source, ServerPlayerEntity target) {
        var request = new Request(source, getConfig().teleportRequestTimeout, Request.Direction.TARGET_TO_SOURCE);
        requests.computeIfAbsent(target.getUuid(), uuid -> new ConcurrentLinkedDeque<>()).add(request);

        var sourceContext = PlaceholderContext.of(source);
        var targetContext = PlaceholderContext.of(target);
        var placeholders = Map.of(
                "requesterPlayer", source.getDisplayName(),
                "acceptButton", Components.button(
                        locale().raw("~accept"),
                        locale().raw("~accept.hover"),
                        "/tpaccept " + source.getGameProfile().getName()),
                "refuseButton", Components.button(
                        locale().raw("~refuse"),
                        locale().raw("~refuse.hover"),
                        "/tpdeny " + source.getGameProfile().getName())
        );

        target.sendMessage(locale().get(
                "pendingTeleportHere",
                targetContext,
                placeholders
        ));

        source.sendMessage(locale().get(
                "requestSent",
                sourceContext
        ));

        NotificationsModule.notify(target);
    }
}
