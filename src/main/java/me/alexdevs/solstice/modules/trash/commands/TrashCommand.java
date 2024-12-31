package me.alexdevs.solstice.modules.trash.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.alexdevs.solstice.api.module.ModCommand;
import me.alexdevs.solstice.modules.trash.TrashModule;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class TrashCommand extends ModCommand<TrashModule> {
    public TrashCommand(TrashModule module) {
        super(module);
    }

    @Override
    public List<String> getNames() {
        return List.of("trash");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> command(String name) {
        return literal(name)
                .requires(require(2))
                .executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();

                    player.openHandledScreen(
                            new SimpleNamedScreenHandlerFactory((syncId, inventory, playerx) ->
                                    GenericContainerScreenHandler.createGeneric9x3(syncId, inventory),
                                    module.locale().get("trash")));

                    return 1;
                });
    }
}
