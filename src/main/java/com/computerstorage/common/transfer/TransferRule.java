package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Immutable rule describing what an automated transfer is allowed to move.
 * Rules are deliberately independent from blocks so they can later be stored
 * in the computer filesystem and edited by the operating system.
 */
public final class TransferRule {
    private final TransferDirection direction;
    private final int priority;
    private final int maxItemsPerOperation;
    private final Predicate<ItemStack> filter;

    public TransferRule(TransferDirection direction, int priority, int maxItemsPerOperation,
                        Predicate<ItemStack> filter) {
        this.direction = Objects.requireNonNull(direction);
        this.priority = priority;
        this.maxItemsPerOperation = Math.max(1, maxItemsPerOperation);
        this.filter = Objects.requireNonNull(filter);
    }

    public TransferDirection direction() { return direction; }
    public int priority() { return priority; }
    public int maxItemsPerOperation() { return maxItemsPerOperation; }
    public boolean accepts(ItemStack stack) { return !stack.isEmpty() && filter.test(stack); }
}
