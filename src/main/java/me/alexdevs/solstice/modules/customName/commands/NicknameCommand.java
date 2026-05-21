package me.alexdevs.solstice.modules.customName.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.modules.customName.CustomNameModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class NicknameCommand extends ModCommand<CustomNameModule> {

    public NicknameCommand(CustomNameModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("nickname", "nick");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .then(argument("nickname", StringArgumentType.string())
                        .executes(this::executeSetSelf)
                )
                .then(literal("clear")
                        .executes(this::executeClearSelf)
                )
                .then(argument("player", StringArgumentType.word())
                        .requires(require("others", 2))
                        .suggests(LocalGameProfile::suggest)
                        .then(argument("nickname", StringArgumentType.string())
                                .executes(this::executeSetOthers)
                        )
                        .then(literal("clear")
                                .executes(this::executeClearOthers)
                        )
                );
    }

    private boolean hasAdvancedPermission(CommandSourceStack source) {
        return Permissions.check(source, getPermissionNode("advanced.base"), 2);
    }

    private boolean hasAdvancedPermissionOthers(CommandSourceStack source) {
        return Permissions.check(source, getPermissionNode("advanced.others"), 3);
    }

    private int executeSetSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var nickname = StringArgumentType.getString(context, "nickname");

        if(nickname.isEmpty()) {
            context.getSource().sendFailure(module.locale().get("errorEmpty"));
            return 0;
        }

        module.setCustomName(
                player,
                nickname,
                hasAdvancedPermission(context.getSource())
        );

        var map = Map.of(
                "player", player.getName(),
                "nickname", Component.nullToEmpty(module.getCustomName(player))
        );
        context.getSource().sendSuccess(() -> module.locale().get("setSelf", map), true);

        return 1;
    }

    private int executeClearSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();
        module.clearCustomName(player);

        var map = Map.of(
                "player", player.getName(),
                "nickname", Component.nullToEmpty(module.getCustomName(player))
        );
        context.getSource().sendSuccess(() -> module.locale().get("clearedSelf", map), true);

        return 1;
    }

    private int executeSetOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var profile = LocalGameProfile.getProfile(context, "player");

        var nickname = StringArgumentType.getString(context, "nickname");

        if(nickname.isEmpty()) {
            context.getSource().sendFailure(module.locale().get("errorEmpty"));
            return 0;
        }

        module.setCustomName(
                PlayerUtils.getId(profile),
                nickname,
                hasAdvancedPermissionOthers(context.getSource())
        );

        var map = Map.of(
                "player", Component.nullToEmpty(PlayerUtils.getName(profile)),
                "nickname", Component.nullToEmpty(module.getCustomName(PlayerUtils.getId(profile)))
        );
        context.getSource().sendSuccess(() -> module.locale().get("setOther", map), true);

        return 1;
    }

    private int executeClearOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var profile = LocalGameProfile.getProfile(context, "player");

        module.clearCustomName(PlayerUtils.getId(profile));

        var map = Map.of(
                "player", Component.nullToEmpty(PlayerUtils.getName(profile)),
                "nickname", Component.nullToEmpty(module.getCustomName(PlayerUtils.getId(profile)))
        );
        context.getSource().sendSuccess(() -> module.locale().get("clearedOther", map), true);

        return 1;
    }
}
