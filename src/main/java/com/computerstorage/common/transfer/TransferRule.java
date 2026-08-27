package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

/** Immutable rule describing what an automated transfer is allowed to move. */
public final class TransferRule {
    private final TransferDirection direction;
    private final int priority;
    private final int maxItemsPerOperation;
    private final int minSourceAmount;
    private final int maxDestinationAmount;
    private final Predicate<ItemStack> filter;
    private final ItemTransferFilter itemFilter;

    public TransferRule(TransferDirection direction, int priority, int maxItemsPerOperation,
                        Predicate<ItemStack> filter) {
        this(direction, priority, maxItemsPerOperation, 0, Integer.MAX_VALUE, filter, null);
    }

    public TransferRule(TransferDirection direction, int priority, int maxItemsPerOperation,
                        int minSourceAmount, int maxDestinationAmount,
                        ItemTransferFilter itemFilter) {
        this(direction, priority, maxItemsPerOperation, minSourceAmount, maxDestinationAmount,
                itemFilter::accepts, Objects.requireNonNull(itemFilter));
    }

    private TransferRule(TransferDirection direction, int priority, int maxItemsPerOperation,
                         int minSourceAmount, int maxDestinationAmount,
                         Predicate<ItemStack> filter, ItemTransferFilter itemFilter) {
        this.direction = Objects.requireNonNull(direction);
        this.priority = priority;
        this.maxItemsPerOperation = Math.max(1, maxItemsPerOperation);
        this.minSourceAmount = Math.max(0, minSourceAmount);
        this.maxDestinationAmount = Math.max(0, maxDestinationAmount);
        this.filter = Objects.requireNonNull(filter);
        this.itemFilter = itemFilter;
    }

    public TransferDirection direction() { return direction; }
    public int priority() { return priority; }
    public int maxItemsPerOperation() { return maxItemsPerOperation; }
    public int minSourceAmount() { return minSourceAmount; }
    public int maxDestinationAmount() { return maxDestinationAmount; }
    public ItemTransferFilter itemFilter() { return itemFilter; }
    public boolean accepts(ItemStack stack) { return !stack.isEmpty() && filter.test(stack); }
}
