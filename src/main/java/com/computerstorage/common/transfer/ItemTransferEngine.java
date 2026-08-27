package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/** Deterministic transfer engine for Forge item inventories. */
public final class ItemTransferEngine {
    private ItemTransferEngine() {}

    public static ItemTransferResult transfer(IItemHandler source, IItemHandler destination,
                                               TransferRule rule) {
        if (source == null || destination == null || rule == null) return ItemTransferResult.none();

        int moved = 0;
        boolean sourceAccepted = false;
        boolean destinationAccepted = false;

        for (int slot = 0; slot < source.getSlots() && moved < rule.maxItemsPerOperation(); slot++) {
            ItemStack available = source.extractItem(slot, rule.maxItemsPerOperation() - moved, true);
            if (available.isEmpty() || available.getCount() < rule.minSourceAmount() || !rule.accepts(available)) continue;
            sourceAccepted = true;

            int destinationLimit = rule.maxDestinationAmount();
            int alreadyPresent = countMatching(destination, available);
            int allowed = Math.min(available.getCount(), Math.max(0, destinationLimit - alreadyPresent));
            if (destinationLimit == Integer.MAX_VALUE) allowed = available.getCount();
            if (allowed <= 0) continue;

            ItemStack remainder = available.copyWithCount(allowed);
            for (int target = 0; target < destination.getSlots() && !remainder.isEmpty(); target++) {
                ItemStack before = remainder.copy();
                remainder = destination.insertItem(target, remainder, false);
                if (remainder.getCount() < before.getCount()) destinationAccepted = true;
            }

            int accepted = allowed - remainder.getCount();
            if (accepted > 0) {
                source.extractItem(slot, accepted, false);
                moved += accepted;
            }
        }

        return new ItemTransferResult(moved, sourceAccepted, destinationAccepted);
    }

    private static int countMatching(IItemHandler handler, ItemStack template) {
        int total = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, template)) total += stack.getCount();
        }
        return total;
    }
}
