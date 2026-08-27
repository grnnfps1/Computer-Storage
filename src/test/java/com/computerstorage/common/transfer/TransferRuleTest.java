package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TransferRuleTest {
    @Test
    void whitelistAcceptsOnlyListedItems() {
        ItemTransferFilter filter = new ItemTransferFilter(ItemFilterMode.WHITELIST,
                Set.of("minecraft:iron_ingot"));
        assertTrue(filter.accepts(new ItemStack(Items.IRON_INGOT)));
        assertFalse(filter.accepts(new ItemStack(Items.GOLD_INGOT)));
    }

    @Test
    void blacklistRejectsListedItems() {
        ItemTransferFilter filter = new ItemTransferFilter(ItemFilterMode.BLACKLIST,
                Set.of("minecraft:iron_ingot"));
        assertFalse(filter.accepts(new ItemStack(Items.IRON_INGOT)));
        assertTrue(filter.accepts(new ItemStack(Items.GOLD_INGOT)));
    }

    @Test
    void allAcceptsAnyNonEmptyStack() {
        assertTrue(ItemTransferFilter.all().accepts(new ItemStack(Items.DIAMOND)));
        assertFalse(ItemTransferFilter.all().accepts(ItemStack.EMPTY));
    }
}
