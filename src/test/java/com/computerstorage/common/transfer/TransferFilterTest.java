package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferFilterTest {
    @Test
    void allAcceptsNonEmptyStacks() {
        assertTrue(new TransferFilter(TransferFilter.Mode.ALL, "").accepts(new ItemStack(Items.IRON_INGOT)));
        assertFalse(new TransferFilter(TransferFilter.Mode.ALL, "").accepts(ItemStack.EMPTY));
    }
}
