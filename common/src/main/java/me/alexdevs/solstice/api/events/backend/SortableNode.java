package me.alexdevs.solstice.api.events.backend;

import java.util.ArrayList;
import java.util.List;

public abstract class SortableNode<N extends SortableNode<N>> {
    final List<N> subsequentNodes = new ArrayList<>();
    final List<N> previousNodes = new ArrayList<>();
    boolean visited = false;

    /**
     * @return Description of this node, used to print the cycle warning.
     */
    protected abstract String getDescription();

    public static <N extends SortableNode<N>> void link(N first, N second) {
        if (first == second) {
            throw new IllegalArgumentException("Cannot link a node to itself!");
        }

        first.subsequentNodes.add(second);
        second.previousNodes.add(first);
    }
}
