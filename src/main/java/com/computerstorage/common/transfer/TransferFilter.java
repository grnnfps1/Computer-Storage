package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;

public record TransferFilter(Mode mode, String itemId) {
    public enum Mode { ALL, WHITELIST, BLACKLIST }

    public boolean accepts(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return switch (mode) {
            case ALL -> true;
            case WHITELIST -> stack.getItem().builtInRegistryHolder().key().location().toString().equals(itemId);
            case BLACKLIST -> !stack.getItem().builtInRegistryHolder().key().location().toString().equals(itemId);
        };
    }
}
