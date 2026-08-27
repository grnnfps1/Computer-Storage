package com.computerstorage.common.transfer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public final class ItemTransferFilter {
    private final ItemFilterMode mode;
    private final Set<String> itemIds;

    public ItemTransferFilter(ItemFilterMode mode, Set<String> itemIds) {
        this.mode = mode == null ? ItemFilterMode.ALL : mode;
        this.itemIds = new HashSet<>(itemIds == null ? Set.of() : itemIds);
    }

    public static ItemTransferFilter all() {
        return new ItemTransferFilter(ItemFilterMode.ALL, Set.of());
    }

    public boolean accepts(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (mode == ItemFilterMode.ALL) return true;
        ResourceLocation id = stack.getItem().builtInRegistryHolder().key().location();
        boolean listed = itemIds.contains(id.toString());
        return mode == ItemFilterMode.WHITELIST ? listed : !listed;
    }

    public ItemFilterMode mode() { return mode; }
    public Set<String> itemIds() { return Set.copyOf(itemIds); }
}
