package me.alexdevs.solstice.modules.utilities.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.utilities.UtilitiesModule;
import me.alexdevs.solstice.modules.utilities.virtualScreenHandlers.VirtualSmithingScreenHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class SmithingCommand extends ModCommand<UtilitiesModule> {
    public SmithingCommand(UtilitiesModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("smithing");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require("smithing", 2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();
                    var screen = new SimpleMenuProvider(
                            (syncId, inventory, p) ->
                                    new VirtualSmithingScreenHandler(syncId, inventory, ContainerLevelAccess.create(player.level(), player.blockPosition())),
                            Component.translatable("container.upgrade"));
                    player.openMenu(screen);
                    player.awardStat(Stats.INTERACT_WITH_SMITHING_TABLE);

                    return 1;
                });
    }
}
