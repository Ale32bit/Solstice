package me.alexdevs.solstice.modules.trash.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.trash.TrashModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class TrashCommand extends ModCommand<TrashModule> {
    public TrashCommand(TrashModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("trash");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrException();

                    player.openMenu(
                            new SimpleMenuProvider((syncId, inventory, playerx) ->
                                    ChestMenu.threeRows(syncId, inventory),
                                    module.locale().get("trash")));

                    return 1;
                });
    }
}
