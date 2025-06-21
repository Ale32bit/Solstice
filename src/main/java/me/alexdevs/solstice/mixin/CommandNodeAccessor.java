package me.alexdevs.solstice.mixin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CommandNode.class, remap = false)
public interface CommandNodeAccessor<S>{
    @Accessor("command")
    @Mutable
    void setCommand(Command<S> command);
}
