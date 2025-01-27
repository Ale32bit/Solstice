package me.alexdevs.solstice.modules.enderchest.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.gui.SimpleGui;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.enderchest.EnderChestModule;
import me.alexdevs.solstice.modules.inventorySee.ImmutableSlot;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EnderChestCommand extends ModCommand<EnderChestModule> {
    public EnderChestCommand(EnderChestModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("enderchest", "ec");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> execute(context, null))
                .then(argument("player", StringArgumentType.word())
                        .requires(require("others", 2))
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> execute(context, LocalGameProfile.getProfile(context, "player"))));
    }

    private int execute(CommandContext<ServerCommandSource> context, @Nullable GameProfile profile) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();
        ServerPlayerEntity target;

        if (profile == null) {
            target = player;
            if (Permissions.check(target, getPermissionNode("exempt"), 3)) {
                source.sendFeedback(() -> module.locale().get("exempt"), false);
                return 0;
            }
        } else {
            target = source.getServer().getPlayerManager().getPlayer(profile.getName());
            if (target == null) {
                if (!PlayerUtils.isOnline(profile.getId()) && !Permissions.check(player, getPermissionNode("offline"), 3)) {
                    source.sendFeedback(() -> module.locale().get("offlineNotAllowed"), false);
                    return 0;
                }
                target = PlayerUtils.loadOfflinePlayer(profile);
                if (Permissions.check(profile, getPermissionNode("exempt"), 3, source.getServer()).getNow(false)) {
                    source.sendFeedback(() -> module.locale().get("exempt"), false);
                    return 0;
                }
            }
        }

        final var isOnline = PlayerUtils.isOnline(target.getUuid());
        final var targetPlayer = target;

        var canEdit = Permissions.check(player, getPermissionNode("edit"), 3);

        var enderChestInventory = target.getEnderChestInventory();

        var container = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public void onClose() {
                if (!isOnline) {
                    PlayerUtils.saveOfflinePlayer(targetPlayer);
                }
            }
        };

        for (var i = 0; i < enderChestInventory.size(); i++) {
            Slot slot;
            if (canEdit) {
                slot = new Slot(enderChestInventory, i, 0, 0);
            } else {
                slot = new ImmutableSlot(enderChestInventory, i, 0, 0);
            }
            container.setSlotRedirect(i, slot);
        }

        if (targetPlayer == player) {
            container.setTitle(Text.translatable("container.enderchest"));
            player.incrementStat(Stats.OPEN_ENDERCHEST);
        } else {
            var map = Map.of(
                    "player", Text.of(target.getGameProfile().getName())
            );
            container.setTitle(module.locale().get("title", map));
        }

        container.open();

        var map = Map.of(
                "player", Text.of(target.getGameProfile().getName())
        );
        source.sendFeedback(() -> module.locale().get("opened", map), false);
        return 1;
    }
}
