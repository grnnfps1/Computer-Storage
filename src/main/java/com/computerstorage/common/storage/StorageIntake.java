package com.computerstorage.common.storage;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * Moves items out of a physical buffer and into the logical index.
 *
 * <p>Kept free of block-entity and level plumbing so the intake rule can be exercised directly:
 * it takes any {@link Container} and a slot range, and never moves more than the index can hold.
 */
public final class StorageIntake {
    private StorageIntake() {}

    /** Drains every slot in the range. */
    public static int drain(Container buffer, int fromSlot, int toSlot, VirtualStorage index) {
        return drain(buffer, fromSlot, toSlot, index, stack -> true);
    }

    /**
     * Drains slots {@code [fromSlot, toSlot)} of the buffer into the index, skipping stacks the
     * filter rejects — the boot disk lives in the same buffer and must not be swallowed.
     *
     * <p>A stack is reduced by exactly what the index accepted, so a full index leaves the buffer
     * untouched rather than destroying items.
     *
     * @return how many items were moved
     */
    public static int drain(Container buffer, int fromSlot, int toSlot, VirtualStorage index,
                            Predicate<ItemStack> accepts) {
        if (buffer == null || index == null || accepts == null) return 0;
        int from = Math.max(0, fromSlot);
        int to = Math.min(toSlot, buffer.getContainerSize());
        int moved = 0;
        for (int slot = from; slot < to; slot++) {
            if (index.available() <= 0) break;
            ItemStack stack = buffer.getItem(slot);
            if (stack.isEmpty() || !accepts.test(stack)) continue;
            int accepted = index.insert(stack);
            if (accepted <= 0) continue;
            moved += accepted;
            stack.shrink(accepted);
            buffer.setItem(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
        }
        if (moved > 0) buffer.setChanged();
        return moved;
    }
}
