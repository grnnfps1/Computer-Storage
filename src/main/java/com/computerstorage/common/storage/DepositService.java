package com.computerstorage.common.storage;

import net.minecraft.world.item.ItemStack;

/**
 * Server-side deposit: what enters the index is only ever what the SSDs have room for.
 *
 * <p>The mirror of {@link WithdrawalService}. The machine state is passed in rather than read here,
 * so the rule that a stopped computer accepts nothing can be exercised without a world.
 */
public final class DepositService {
    private DepositService() {}

    /**
     * Moves as much of the stack into the index as capacity allows.
     *
     * @param machineRunning whether the computer is running; a stopped machine accepts nothing
     * @return the remainder that did not fit, empty when the whole stack went in. The caller is
     *         expected to put this back wherever the stack came from, so nothing is ever destroyed.
     */
    public static ItemStack deposit(VirtualStorage index, ItemStack stack, boolean machineRunning) {
        if (stack == null) return ItemStack.EMPTY;
        if (index == null || stack.isEmpty() || !machineRunning) return stack;

        int moved = index.insert(stack);
        if (moved <= 0) return stack;
        if (moved >= stack.getCount()) return ItemStack.EMPTY;
        return stack.copyWithCount(stack.getCount() - moved);
    }

    /**
     * Deposits at most {@code amount} of the stack, leaving the rest where it was. Used by the
     * right-click path, which offers a single item rather than the whole handful.
     */
    public static ItemStack depositAmount(VirtualStorage index, ItemStack stack, int amount,
                                          boolean machineRunning) {
        if (stack == null) return ItemStack.EMPTY;
        if (index == null || stack.isEmpty() || amount <= 0 || !machineRunning) return stack;

        int offered = Math.min(amount, stack.getCount());
        ItemStack leftover = deposit(index, stack.copyWithCount(offered), true);
        int accepted = offered - (leftover.isEmpty() ? 0 : leftover.getCount());
        if (accepted <= 0) return stack;
        return stack.copyWithCount(stack.getCount() - accepted);
    }
}
