package me.alexdevs.solstice.neoforge;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.events.proxy.*;
import me.alexdevs.solstice.api.platform.ModInfo;
import me.alexdevs.solstice.api.platform.PlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.nio.file.Path;

public class NeoForgePlatform extends PlatformHelper {
    public void init() {
        NeoForge.EVENT_BUS.addListener(
                RegisterCommandsEvent.class,
                (ev) -> ProxyCommandRegistrationCallback.EVENT.invoker()
                        .onRegister(ev.getDispatcher(), ev.getBuildContext(), ev.getCommandSelection())
        );

        NeoForge.EVENT_BUS.addListener(
                ServerStartingEvent.class,
                (ev) -> ProxyServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(ev.getServer())
        );

        NeoForge.EVENT_BUS.addListener(
                ServerStartedEvent.class,
                (ev) -> ProxyServerLifecycleEvents.SERVER_STARTED.invoker().onServerStarted(ev.getServer())
        );

        NeoForge.EVENT_BUS.addListener(
                ServerStoppingEvent.class,
                (ev) -> ProxyServerLifecycleEvents.SERVER_STOPPING.invoker().onServerStopping(ev.getServer())
        );

        NeoForge.EVENT_BUS.addListener(
                ServerStoppedEvent.class,
                (ev) -> ProxyServerLifecycleEvents.SERVER_STOPPED.invoker().onServerStopped(ev.getServer())
        );

        NeoForge.EVENT_BUS.addListener(
                ServerTickEvent.Pre.class,
                (ev) -> ProxyServerTickEvents.START_SERVER_TICK.invoker().onStartTick(ev.getServer())
        );

        NeoForge.EVENT_BUS.addListener(
                ServerTickEvent.Post.class,
                (ev) -> ProxyServerTickEvents.END_SERVER_TICK.invoker().onEndTick(ev.getServer())
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerInteractEvent.RightClickItem.class, (ev) -> {
                    var result = ProxyUseItemCallback.EVENT.invoker()
                            .interact(ev.getEntity(), ev.getLevel(), ev.getHand())
                            .getResult();

                    if (result != InteractionResult.PASS) {
                        ev.setCanceled(true);
                        ev.setCancellationResult(result);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerInteractEvent.RightClickBlock.class, (ev) -> {
                    var result = ProxyUseBlockCallback.EVENT.invoker()
                            .interact(ev.getEntity(), ev.getLevel(), ev.getHand(), ev.getHitVec());

                    if (result != InteractionResult.PASS) {
                        ev.setCanceled(true);
                        ev.setCancellationResult(result);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerInteractEvent.EntityInteract.class, (ev) -> {
                    var result = ProxyUseEntityCallback.EVENT.invoker()
                            .interact(ev.getEntity(), ev.getLevel(), ev.getHand(), ev.getTarget(), null);

                    if (result != InteractionResult.PASS) {
                        ev.setCanceled(true);
                        ev.setCancellationResult(result);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerInteractEvent.EntityInteractSpecific.class, (ev) -> {
                    var result = ProxyUseEntityCallback.EVENT.invoker().interact(
                            ev.getEntity(),
                            ev.getLevel(),
                            ev.getHand(),
                            ev.getTarget(),
                            new EntityHitResult(ev.getTarget(), ev.getLocalPos().add(ev.getTarget().position()))
                    );

                    if (result != InteractionResult.PASS) {
                        ev.setCanceled(true);
                        ev.setCancellationResult(result);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                BlockEvent.BreakEvent.class, (ev) -> {
                    var player = ev.getPlayer();
                    var level = player.level();

                    var result = ProxyPlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(
                            level,
                            player,
                            ev.getPos(),
                            level.getBlockState(ev.getPos()),
                            level.getBlockEntity(ev.getPos())
                    );

                    if (!result) {
                        ev.setCanceled(true);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerInteractEvent.LeftClickBlock.class, (ev) -> {
                    if (ev.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
                        var result = ProxyAttackBlockCallback.EVENT.invoker()
                                .interact(ev.getEntity(), ev.getLevel(), ev.getHand(), ev.getPos(), ev.getFace());

                        if (result != InteractionResult.PASS) {
                            ev.setUseBlock(result == InteractionResult.SUCCESS ? TriState.TRUE : TriState.FALSE);
                            ev.setUseItem(result == InteractionResult.SUCCESS ? TriState.TRUE : TriState.FALSE);
                        }
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                AttackEntityEvent.class, (ev) -> {
                    var result = ProxyAttackEntityCallback.EVENT.invoker()
                            .interact(
                                    ev.getEntity(),
                                    ev.getEntity().level(),
                                    InteractionHand.MAIN_HAND,
                                    ev.getTarget(),
                                    null
                            );

                    if (result != InteractionResult.PASS) {
                        ev.setCanceled(true);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerEvent.PlayerLoggedInEvent.class, (ev) -> {
                    if (ev.getEntity() instanceof ServerPlayer player) {
                        ProxyServerPlayConnectionEvents.JOIN.invoker().onJoin(player, player.server);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                PlayerEvent.PlayerLoggedOutEvent.class, (ev) -> {
                    if (ev.getEntity() instanceof ServerPlayer player) {
                        ProxyServerPlayConnectionEvents.DISCONNECT.invoker().onDisconnect(player, player.server);
                    }
                }
        );

        NeoForge.EVENT_BUS.addListener(
                CanContinueSleepingEvent.class, (ev) -> {
                    ev.getEntity().getSleepingPos().ifPresent(sleepingPos -> {
                        if (ev.getEntity() instanceof Player player) {
                            InteractionResult result = ProxyEntitySleepEvents.ALLOW_SLEEP_TIME.invoker()
                                    .allowSleepTime(player, sleepingPos, !player.level().isDay());
                            if (result != InteractionResult.PASS) {
                                ev.setContinueSleeping(result.consumesAction());
                            }
                        }
                    });
                }
        );
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isNativeForge() {
        return true;
    }

    @Override
    public Object getModContainer() {
        return ModList.get().getModContainerById(Solstice.MOD_ID).orElseThrow();
    }

    @Override
    public String getModVersion() {
        return ModList.get().getModContainerById(Solstice.MOD_ID).orElseThrow().getModInfo().getVersion().toString();
    }

    @Override
    public ModInfo getModInfo(String id) {
        return ModList.get()
                .getModContainerById(id)
                .map(it -> new ModInfo(it.getModInfo().getDisplayName(), it.getModInfo().getVersion().toString()))
                .orElse(null);
    }
}
