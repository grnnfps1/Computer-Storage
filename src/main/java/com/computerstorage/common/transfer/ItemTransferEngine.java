package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Small, deterministic transfer engine used by future network nodes.
 * It moves real ItemStacks through Forge's IItemHandler capability, making
 * the system compatible with vanilla containers and other Forge mods.
 */
public final class ItemTransferEngine {
    private ItemTransferEngine() {}

    public static ItemTransferResult transfer(IItemHandler source, IItemHandler destination,
                                               TransferRule rule) {
        if (source == null || destination == null || rule == null) {
            return ItemTransferResult.none();
        }

        int moved = 0;
        boolean sourceAccepted = false;
        boolean destinationAccepted = false;

        for (int slot = 0; slot < source.getSlots() && moved < rule.maxItemsPerOperation(); slot++) {
            ItemStack available = source.extractItem(slot, rule.maxItemsPerOperation() - moved, true);
            if (available.isEmpty() || !rule.accepts(available)) continue;
            sourceAccepted = true;

            ItemStack remainder = available.copy();
            for (int target = 0; target < destination.getSlots() && !remainder.isEmpty(); target++) {
                ItemStack before = remainder.copy();
                remainder = destination.insertItem(target, remainder, false);
                if (remainder.getCount() < before.getCount()) destinationAccepted = true;
            }

            int accepted = available.getCount() - remainder.getCount();
            if (accepted > 0) {
                source.extractItem(slot, accepted, false);
                moved += accepted;
            }
        }

        return new ItemTransferResult(moved, sourceAccepted, destinationAccepted);
    }
}
