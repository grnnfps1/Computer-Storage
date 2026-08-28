package com.computerstorage.common.hardware;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Spreads a stack across repeated sockets instead of dumping it into the first one.
 *
 * <p>Four RAM sticks belong in four sockets, not stacked in one, so shift-clicking a stack of RAM
 * has to walk the range placing a single item per empty socket.
 */
public final class SlotDistribution {
    private SlotDistribution() {}

    /**
     * Moves one item into each empty slot of {@code [from, to)} that accepts it, taking from the
     * source stack.
     *
     * @return how many slots were filled
     */
    public static int spreadOnePerSlot(Container target, int from, int to, ItemStack source) {
        if (target == null || source == null || source.isEmpty()) return 0;
        int filled = 0;
        int first = Math.max(0, from);
        int last = Math.min(to, target.getContainerSize());
        for (int slot = first; slot < last && !source.isEmpty(); slot++) {
            if (!target.getItem(slot).isEmpty()) continue;
            if (!target.canPlaceItem(slot, source)) continue;
            target.setItem(slot, source.split(1));
            filled++;
        }
        return filled;
    }
}
