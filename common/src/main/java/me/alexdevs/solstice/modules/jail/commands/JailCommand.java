package me.alexdevs.solstice.modules.jail.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.permissions.Permissions;
import me.alexdevs.solstice.core.coreModule.data.CorePlayerData;
import me.alexdevs.solstice.modules.jail.JailModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
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
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require("jail", 2))
                .then(Commands.argument("user", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
                        .then(Commands.argument("jail", StringArgumentType.word())
                                .suggests(this::suggestJails)
                                .executes(context -> execute(context, 0, null))
                                .then(Commands.argument("duration", TimeSpan.timeSpan())
                                        .suggests(TimeSpan::suggest)
                                        .executes(context -> execute(context, TimeSpan.getTimeSpan(context, "duration"), null))
                                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                                                .executes(context -> execute(context, TimeSpan.getTimeSpan(context, "duration"), StringArgumentType.getString(context, "reason")))
                                        )
                                )
                        )
                );
    }

    private int execute(CommandContext<CommandSourceStack> context, int seconds, @Nullable String reason) throws CommandSyntaxException {
        var source = context.getSource();
        var profile = LocalGameProfile.getProfile(context, "user");

        var data = module.getPlayer(profile.getId());
        var coreData = Solstice.playerData.get(profile.getId()).getData(CorePlayerData.class);

        if (data.jailed) {
            source.sendSuccess(() -> module.locale().get("alreadyJailed"), false);
            return 0;
        }

        var jailName = StringArgumentType.getString(context, "jail");

        var jails = module.getJails();
        if (!jails.containsKey(jailName)) {
            source.sendSuccess(() -> module.locale().get("jailNotFound"), false);
            return 0;
        }

        Permissions.check(profile, getPermissionNode("exempt")).thenAccept(granted -> {
            if (granted) {
                source.sendSuccess(() -> module.locale().get("playerExempt"), false);
                return;
            }

            var player = source.getServer().getPlayerList().getPlayer(profile.getId());

            data.jailed = true;
            data.jailedBy = source.isPlayer() ? source.getPlayer().getUUID() : new UUID(0L, 0L);
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
                    "player", Component.nullToEmpty(profile.getName()),
                    "jail", Component.nullToEmpty(jailName),
                    "duration", Component.nullToEmpty(TimeSpan.toLongString(seconds)),
                    "reason", Component.nullToEmpty(reason)
            );

            Component text;
            if (seconds > 0) {
                if (reason != null) {
                    text = module.locale().get("jailedForWithReason", map);
                } else {
                    text = module.locale().get("jailedFor", map);
                }
            } else {
                text = module.locale().get("jailed", map);
            }

            source.sendSuccess(() -> text, true);

            if (player != null) {
                module.sendToJail(player);
            }
        });

        return 1;
    }

    private CompletableFuture<Suggestions> suggestJails(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        var jails = module.getJails().keySet().stream();
        return SharedSuggestionProvider.suggest(jails, builder);
    }
}
