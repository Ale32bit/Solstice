package me.alexdevs.solstice.api.module;

import me.alexdevs.solstice.api.utils.SolsticeIdentifier;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Debug {
    public static final HashSet<CommandDebug> commandDebugList = new HashSet<>();

    public record CommandDebug(SolsticeIdentifier module, String command, List<String> commands, String permission) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CommandDebug that)) return false;
            return Objects.equals(module, that.module) && Objects.equals(command, that.command) && Objects.equals(permission, that.permission);
        }

        @Override
        public int hashCode() {
            return Objects.hash(module, command, permission);
        }
    }
}
