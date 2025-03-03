package me.alexdevs.solstice.modules.seen.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.alexdevs.solstice.api.ServerLocation;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.core.coreModule.CoreModule;
import me.alexdevs.solstice.modules.seen.SeenModule;
import me.alexdevs.solstice.api.text.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SeenCommand extends ModCommand<SeenModule> {
    public SeenCommand(SeenModule module) {
        super(module);
    }

    public static String getPositionAsString(@Nullable ServerLocation pos) {
        if (pos == null)
            return "Unknown position";

        return String.format("%.01f %.01f %.01f, %s", pos.getX(), pos.getY(), pos.getZ(), pos.getWorld());
    }

    @Override
    public List<String> getNames() {
        return List.of("seen", "playerinfo");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(true))
                .then(argument("player", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            var source = context.getSource();

                            var profile = LocalGameProfile.getProfile(context, "player");

                            boolean extended;
                            if (context.getSource().isPlayer()) {
                                extended = Permissions.check(context.getSource().getPlayer(), getPermissionNode("extended"), 2);
                            } else {
                                extended = true;
                            }

                            var config = CoreModule.getConfig();

                            var dateFormatter = new SimpleDateFormat(config.dateTimeFormat);
                            var player = source.getServer().getPlayerList().getPlayer(profile.getId());
                            var playerData = CoreModule.getPlayerData(profile.getId());

                            if(playerData.firstJoinedDate == null) {
                                source.sendSuccess(() -> module.locale().get("playerNotFound"), false);
                                return 0;
                            }

                            ServerLocation location;
                            if (player == null) {
                                location = playerData.logoffPosition;
                            } else {
                                location = new ServerLocation(player);
                            }

                            var firstSeenDate = playerData.firstJoinedDate != null ? dateFormatter.format(playerData.firstJoinedDate) : module.locale().raw("neverJoined");
                            var lastSeenDate = playerData.lastSeenDate != null ? dateFormatter.format(playerData.lastSeenDate) : module.locale().raw("unknown");
                            var ipAddress = playerData.ipAddress != null ? playerData.ipAddress : module.locale().raw("unknown");

                            Map<String, Component> map = Map.of(
                                    "username", Component.nullToEmpty(profile.getName()),
                                    "uuid", Component.nullToEmpty(profile.getId().toString()),
                                    "firstSeenDate", Component.nullToEmpty(firstSeenDate),
                                    "lastSeenDate", Component.nullToEmpty(player != null ? module.locale().raw("online") : lastSeenDate),
                                    "ipAddress", Component.nullToEmpty(ipAddress),
                                    "location", Component.nullToEmpty(getPositionAsString(location))
                            );

                            var outputString = module.locale().raw("base");
                            if (extended) {
                                outputString += "\n";
                                outputString += module.locale().raw("extended");
                            }

                            final var finalOutput = outputString;
                            if (player != null) {
                                source.sendSuccess(() -> Format.parse(finalOutput, PlaceholderContext.of(player), map), false);
                            } else {
                                source.sendSuccess(() -> Format.parse(finalOutput, PlaceholderContext.of(source.getServer()), map), false);
                            }

                            return 1;
                        }));
    }
}
