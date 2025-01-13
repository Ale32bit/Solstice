package me.alexdevs.solstice.modules.jail.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.command.SingleGameProfile;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.data.CorePlayerData;
import me.alexdevs.solstice.modules.jail.JailModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JailCommand extends ModCommand<JailModule> {
    public JailCommand(JailModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("jail");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return CommandManager.literal(name)
                .requires(require("jail", 2))
                .then(CommandManager.argument("user", GameProfileArgumentType.gameProfile())
                        .then(CommandManager.argument("jail", StringArgumentType.word())
                                .suggests(this::suggestJails)
                                .executes(context -> execute(context, 0, null))
                                .then(CommandManager.argument("duration", TimeSpan.timeSpan())
                                        .suggests(TimeSpan::suggest)
                                        .executes(context -> execute(context, TimeSpan.getTimeSpan(context, "duration"), null))
                                        .then(CommandManager.argument("reason", StringArgumentType.greedyString())
                                                .executes(context -> execute(context, TimeSpan.getTimeSpan(context, "duration"), StringArgumentType.getString(context, "reason")))
                                        )
                                )
                        )
                );
    }

    private int execute(CommandContext<ServerCommandSource> context, int seconds, @Nullable String reason) throws CommandSyntaxException {
        var source = context.getSource();
        var profile = SingleGameProfile.getProfile(context, "user");

        var data = module.getPlayer(profile.getId());
        var coreData = Solstice.playerData.get(profile.getId()).getData(CorePlayerData.class);

        if (data.jailed) {
            source.sendFeedback(() -> module.locale().get("alreadyJailed"), false);
            return 0;
        }

        var jailName = StringArgumentType.getString(context, "jail");

        var jails = module.getJails();
        if (!jails.containsKey(jailName)) {
            source.sendFeedback(() -> module.locale().get("jailNotFound"), false);
            return 0;
        }

        Permissions.check(profile, getPermissionNode("exempt")).thenAccept(granted -> {
            if (granted) {
                source.sendFeedback(() -> module.locale().get("playerExempt"), false);
                return;
            }

            var player = source.getServer().getPlayerManager().getPlayer(profile.getId());

            data.jailed = true;
            data.jailedBy = source.isExecutedByPlayer() ? source.getPlayer().getUuid() : new UUID(0L, 0L);
            data.jailedOn = new Date();
            data.jailName = jailName;
            data.jailTime = seconds;
            data.jailReason = reason;

            if (player != null) {
                data.previousLocation = new ServerLocation(player);
            } else {
                data.previousLocation = coreData.logoffPosition;
            }

            var map = Map.of(
                    "player", Text.of(profile.getName()),
                    "jail", Text.of(jailName),
                    "duration", Text.of(TimeSpan.toLongString(seconds)),
                    "reason", Text.of(reason)
            );

            Text text;
            if (seconds > 0) {
                if (reason != null) {
                    text = module.locale().get("jailedForWithReason", map);
                } else {
                    text = module.locale().get("jailedFor", map);
                }
            } else {
                text = module.locale().get("jailed", map);
            }

            source.sendFeedback(() -> text, true);

            if (player != null) {
                module.sendToJail(player);
            }
        });

        return 1;
    }

    private CompletableFuture<Suggestions> suggestJails(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        var jails = module.getJails().keySet().stream();
        return CommandSource.suggestMatching(jails, builder);
    }
}
