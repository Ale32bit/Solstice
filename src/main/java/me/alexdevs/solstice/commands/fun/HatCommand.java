package me.alexdevs.solstice.commands.fun;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.util.Format;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class HatCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var rootCommand = literal("hat")
                .requires(Permissions.require("solstice.command.hat", 2))
                        .executes(context ->
                                execute(context, 1)
                        );


        dispatcher.register(rootCommand);
    }

    private static int execute(CommandContext<ServerCommandSource> context, int times) throws CommandSyntaxException {

        var player = context.getSource().getPlayerOrThrow();
        var hatItem = player.getMainHandStack();
        if(hatItem.getItem() != Items.AIR){
            var inventory =  player.getInventory();
            var oldHadItem = inventory.armor.get(3);
            inventory.setStack(inventory.selectedSlot, oldHadItem.copyAndEmpty());
           inventory.armor.set(3, hatItem.copyAndEmpty());
            context.getSource().sendFeedback(() ->
                    Format.parse(
                            Solstice.locale().commands.hat.hatPutOn
                    ), false);
        }
        else{
            context.getSource().sendFeedback(() ->
                    Format.parse(
                            Solstice.locale().commands.hat.noItemInHand
                    ), false);
        }


        return 1;
    }
}
