package com.computerstorage.common.storage;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Search and withdrawal arithmetic for a storage index listing, kept free of GUI and network code. */
public final class IndexQuery {
    private IndexQuery() {}

    /**
     * Filters a listing by a case-insensitive substring of the item's display name. A blank query
     * returns the listing unchanged.
     */
    public static List<ItemStack> filter(List<ItemStack> listing, String query) {
        if (listing == null) return List.of();
        if (query == null || query.isBlank()) return List.copyOf(listing);
        String needle = query.trim().toLowerCase(Locale.ROOT);
        List<ItemStack> matches = new ArrayList<>();
        for (ItemStack stack : listing) {
            if (stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(needle)) matches.add(stack);
        }
        return matches;
    }

    /**
     * How much a click should take: one item on a plain click, a full stack on shift-click, never
     * more than the index actually holds.
     *
     * @param stored how many of that item the index holds
     */
    public static int withdrawAmount(ItemStack template, int stored, boolean wholeStack) {
        if (template == null || template.isEmpty() || stored <= 0) return 0;
        int wanted = wholeStack ? Math.max(1, template.getMaxStackSize()) : 1;
        return Math.min(wanted, stored);
    }
}
