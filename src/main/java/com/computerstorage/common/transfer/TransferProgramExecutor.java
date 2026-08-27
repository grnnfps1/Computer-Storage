package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/** Executes a single programmed route against Forge item-handler inventories. */
public final class TransferProgramExecutor {
    public int execute(TransferProgram program, IItemHandler source, IItemHandler destination) {
        if (source == null || destination == null) return 0;
        int moved = 0;
        for (int slot = 0; slot < source.getSlots() && moved < program.maxItemsPerOperation(); slot++) {
            ItemStack simulated = source.extractItem(slot, program.maxItemsPerOperation() - moved, true);
            if (simulated.isEmpty() || !program.filter().accepts(simulated)) continue;
            int sourceTotal = countItem(source, simulated);
            if (sourceTotal < program.minSourceAmount()) continue;
            int allowed = Math.min(simulated.getCount(), program.maxItemsPerOperation() - moved);
            ItemStack candidate = simulated.copy();
            candidate.setCount(allowed);
            ItemStack remainder = destination.insertItem(0, candidate, true);
            int accepted = allowed - remainder.getCount();
            if (accepted <= 0) continue;
            ItemStack extracted = source.extractItem(slot, accepted, false);
            ItemStack leftover = destination.insertItem(0, extracted, false);
            moved += extracted.getCount() - leftover.getCount();
        }
        return moved;
    }

    private int countItem(IItemHandler handler, ItemStack target) {
        int count = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (ItemStack.isSameItemSameTags(stack, target)) count += stack.getCount();
        }
        return count;
    }
}
