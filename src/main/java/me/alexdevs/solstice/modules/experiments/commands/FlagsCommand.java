package me.alexdevs.solstice.modules.experiments.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.command.Flags;
import me.alexdevs.solstice.api.command.flags.ArgumentFlag;
import me.alexdevs.solstice.api.command.flags.DoubleFlag;
import me.alexdevs.solstice.api.command.flags.Flag;
import me.alexdevs.solstice.api.command.flags.StringFlag;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.experiments.ExperimentsModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import java.util.List;

public class FlagsCommand extends ModCommand<ExperimentsModule> {
    public FlagsCommand(ExperimentsModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("flags");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return Commands.literal(name)
                .requires(require(true))
                .then(Commands.argument("flags", StringArgumentType.greedyString())
                        .executes(context -> {
                            var source = context.getSource();

                            var testFlag = Flag.of("test");
                            var stringFlag = new StringFlag("string", List.of('s'));
                            var numberFlag = new DoubleFlag("number", List.of('n'));

                            Flags.parse(StringArgumentType.getString(context, "flags"), testFlag, stringFlag, numberFlag);

                            for (var flag : List.of(testFlag, stringFlag, numberFlag)) {
                                if (flag.isUsed()) {
                                    if (flag.acceptsValue() && flag instanceof ArgumentFlag<?> argFlag) {
                                        var value = argFlag.getValue();
                                        source.sendSystemMessage(Component.nullToEmpty(String.format("Flag %s: %s", flag.getName(), value)));

                                    } else {
                                        source.sendSystemMessage(Component.nullToEmpty(String.format("Flag %s", flag.getName())));
                                    }
                                }
                            }

                            return 1;
                        })
                );
    }


}
