package com.computerstorage.common.transfer;

import com.computerstorage.test.BootstrapMinecraft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@BootstrapMinecraft
class ItemTransferEngineTest {
    @Test
    void movesOnlyItemsAcceptedByRule() {
        ItemStackHandler source = new ItemStackHandler(1);
        ItemStackHandler destination = new ItemStackHandler(1);
        source.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 32));

        TransferRule rule = new TransferRule(
                TransferDirection.EXPORT,
                10,
                16,
                stack -> stack.is(Items.IRON_INGOT)
        );

        ItemTransferResult result = ItemTransferEngine.transfer(source, destination, rule);

        assertEquals(16, result.moved());
        assertEquals(16, source.getStackInSlot(0).getCount());
        assertEquals(16, destination.getStackInSlot(0).getCount());
    }

    @Test
    void rejectsItemsOutsideTheFilter() {
        ItemStackHandler source = new ItemStackHandler(1);
        ItemStackHandler destination = new ItemStackHandler(1);
        source.setStackInSlot(0, new ItemStack(Items.GOLD_INGOT, 8));

        TransferRule rule = new TransferRule(TransferDirection.EXPORT, 0, 64,
                stack -> stack.is(Items.IRON_INGOT));

        ItemTransferResult result = ItemTransferEngine.transfer(source, destination, rule);

        assertEquals(0, result.moved());
        assertEquals(8, source.getStackInSlot(0).getCount());
        assertEquals(0, destination.getStackInSlot(0).getCount());
    }
}
