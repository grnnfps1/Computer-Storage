package com.computerstorage.common.storage;

import net.minecraft.world.item.ItemStack;

/** Server-side withdrawal: what leaves the index is exactly what the player can carry away. */
public final class WithdrawalService {
    private WithdrawalService() {}

    /**
     * Takes at most {@code amount} of the template out of the index and hands it to the receiver,
     * returning whatever the receiver could not take to the index rather than dropping it.
     *
     * @param receiver accepts a stack and returns the remainder it could not hold
     * @return how many items the player actually received
     */
    public static int withdraw(VirtualStorage index, ItemStack template, int amount,
                               java.util.function.UnaryOperator<ItemStack> receiver) {
        if (index == null || template == null || template.isEmpty() || amount <= 0) return 0;
        int available = index.count(template);
        int take = Math.min(amount, available);
        if (take <= 0) return 0;

        ItemStack taken = index.extract(template, take);
        if (taken.isEmpty()) return 0;

        ItemStack leftover = receiver.apply(taken);
        int returned = leftover.isEmpty() ? 0 : leftover.getCount();
        if (returned > 0) index.insert(leftover);
        return taken.getCount() - returned;
    }
}
