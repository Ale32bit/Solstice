package me.alexdevs.solstice.modules.seen.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.ServerPosition;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.alexdevs.solstice.modules.seen.SeenModule;
import me.alexdevs.solstice.api.text.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SeenCommand extends ModCommand<SeenModule> {
    public SeenCommand(SeenModule module) {
        super(module);
    }

    public static String getPositionAsString(@Nullable ServerPosition pos) {
        if (pos == null)
            return "Unknown position";

        return String.format("%.01f %.01f %.01f, %s", pos.getX(), pos.getY(), pos.getZ(), pos.getWorld());
    }

    @Override
    public List<String> getNames() {
        return List.of("seen", "playerinfo");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("player", StringArgumentType.word())
                        .executes(context -> {
                            var targetName = StringArgumentType.getString(context, "player");
                            var source = context.getSource();
                            source.getServer().getUserCache().findByNameAsync(targetName, (profile) -> {
                                if (profile.isEmpty()) {
                                    source.sendFeedback(() -> module.locale().get("playerNotFound"), false);
                                    return;
                                }
                                boolean extended;
                                if (context.getSource().isExecutedByPlayer()) {
                                    extended = Permissions.check(context.getSource().getPlayer(), getPermissionNode("extended"), 2);
                                } else {
                                    extended = true;
                                }

                                var config = CoreModule.getConfig();

                                var dateFormatter = new SimpleDateFormat(config.dateTimeFormat);
                                var player = source.getServer().getPlayerManager().getPlayer(profile.get().getId());
                                var playerData = CoreModule.getPlayerData(profile.get().getId());

                                ServerPosition location;
                                if (player == null) {
                                    location = playerData.logoffPosition;
                                } else {
                                    location = new ServerPosition(player);
                                }

                                Map<String, Text> map = Map.of(
                                        "username", Text.of(profile.get().getName()),
                                        "uuid", Text.of(profile.get().getId().toString()),
                                        "firstSeenDate", Text.of(dateFormatter.format(playerData.firstJoinedDate)),
                                        "lastSeenDate", player != null ? Text.of("online") : Text.of(dateFormatter.format(playerData.lastSeenDate)),
                                        "ipAddress", Text.of(playerData.ipAddress),
                                        "location", Text.of(getPositionAsString(location))
                                );

                                var outputString = module.locale().raw("base");
                                if (extended) {
                                    outputString += "\n";
                                    outputString += module.locale().raw("extended");
                                }

                                final var finalOutput = outputString;
                                if (player != null) {
                                    source.sendFeedback(() -> Format.parse(finalOutput, PlaceholderContext.of(player), map), false);
                                } else {
                                    source.sendFeedback(() -> Format.parse(finalOutput, PlaceholderContext.of(source.getServer()), map), false);
                                }
                            });

                            return 1;
                        }));
    }
}
