package me.alexdevs.solstice.modules.jail.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.command.LocalGameProfile;
import me.alexdevs.solstice.api.command.TimeSpan;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.api.utils.PlayerUtils;
import me.alexdevs.solstice.core.coreModule.data.CoreConfig;
import me.alexdevs.solstice.modules.jail.JailModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CheckJailCommand extends ModCommand<JailModule> {

    public CheckJailCommand(JailModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("checkjail");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(2))
                .then(Commands.argument("user", StringArgumentType.word())
                        .suggests(LocalGameProfile::suggest)
                        .executes(context -> {
                            var profile = LocalGameProfile.getProfile(context, "user");
                            var data = module.getPlayer(PlayerUtils.getId(profile));

                            if (!data.jailed) {
                                context.getSource().sendSuccess(() -> module.locale().get("notJailed"), false);
                                return 0;
                            }

                            String operator;
                            if (new UUID(0, 0).equals(data.jailedBy)) {
                                operator = "Server";
                            } else {
                                var opProfile = PlayerUtils.getProfile(context.getSource().getServer(), data.jailedBy);
                                if (opProfile.isPresent()) {
                                    operator = PlayerUtils.getName(opProfile.get());
                                } else {
                                    operator = data.jailedBy != null ? data.jailedBy.toString() : "Unknown";
                                }
                            }

                            String reason;
                            if (data.jailReason != null) {
                                reason = data.jailReason;
                            } else {
                                reason = module.locale().raw("infoJailReasonEmpty");
                            }

                            String duration;
                            if (data.jailTime == 0) {
                                duration = module.locale().raw("infoJailedForEmpty");
                            } else {
                                duration = TimeSpan.toLongString(data.jailTime);
                            }

                            var coreConfig = Solstice.configManager.getData(CoreConfig.class);
                            var df = new SimpleDateFormat(coreConfig.dateTimeFormat);

                            var map = Map.of(
                                    "player", Component.nullToEmpty(PlayerUtils.getName(profile)),
                                    "jail", Component.nullToEmpty(data.jailName),
                                    "operator", Component.nullToEmpty(operator),
                                    "reason", Component.nullToEmpty(reason),
                                    "duration", Component.nullToEmpty(duration),
                                    "date", Component.nullToEmpty(df.format(data.jailedOn))

                            );

                            var text = Component.empty();
                            text.append(module.locale().get("infoHeader", map));
                            text.append("\n");
                            text.append(module.locale().get("infoJailedAt", map));
                            text.append("\n");
                            text.append(module.locale().get("infoJailedBy", map));
                            text.append("\n");
                            text.append(module.locale().get("infoJailReason", map));
                            text.append("\n");
                            text.append(module.locale().get("infoJailedFor", map));
                            text.append("\n");
                            text.append(module.locale().get("infoJailedOn", map));

                            context.getSource().sendSuccess(() -> text, false);

                            return 1;
                        })
                );
    }
}
