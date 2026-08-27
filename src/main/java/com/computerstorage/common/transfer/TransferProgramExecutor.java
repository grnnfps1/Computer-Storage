package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/** Executes a single programmed route against Forge item-handler inventories. */
public final class TransferProgramExecutor {
    public int execute(TransferProgram program, IItemHandler source, IItemHandler destination) {
        if (program == null || source == null || destination == null) return 0;
        int moved = 0;
        for (int slot = 0; slot < source.getSlots() && moved < program.maxItemsPerOperation(); slot++) {
            ItemStack simulated = source.extractItem(slot, program.maxItemsPerOperation() - moved, true);
            if (simulated.isEmpty() || !program.filter().accepts(simulated)) continue;
            int sourceTotal = countItem(source, simulated);
            if (sourceTotal < program.minSourceAmount()) continue;

            int allowed = Math.min(simulated.getCount(), program.maxItemsPerOperation() - moved);
            if (program.maxDestinationAmount() > 0) {
                int destinationTotal = countItem(destination, simulated);
                allowed = Math.min(allowed, Math.max(0, program.maxDestinationAmount() - destinationTotal));
            }
            if (allowed <= 0) continue;

            ItemStack candidate = simulated.copyWithCount(allowed);
            int accepted = simulateDestinationCapacity(destination, candidate);
            if (accepted <= 0) continue;

            ItemStack extracted = source.extractItem(slot, accepted, false);
            int remaining = extracted.getCount();
            for (int target = 0; target < destination.getSlots() && remaining > 0; target++) {
                ItemStack before = extracted.copy();
                extracted = destination.insertItem(target, extracted, false);
                remaining -= before.getCount() - extracted.getCount();
            }
            moved += accepted - extracted.getCount();
        }
        return moved;
    }

    private int simulateDestinationCapacity(IItemHandler destination, ItemStack stack) {
        int accepted = 0;
        ItemStack remainder = stack.copy();
        for (int slot = 0; slot < destination.getSlots() && !remainder.isEmpty(); slot++) {
            remainder = destination.insertItem(slot, remainder, true);
        }
        accepted = stack.getCount() - remainder.getCount();
        return accepted;
    }

    private int countItem(IItemHandler handler, ItemStack target) {
        int count = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, target)) count += stack.getCount();
        }
        return count;
    }
}
