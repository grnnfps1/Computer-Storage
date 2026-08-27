package com.computerstorage.common.transfer;

import com.computerstorage.test.BootstrapMinecraft;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@BootstrapMinecraft
class TransferProgramExecutorTest {
    @Test
    void transfersOnlyFilteredItemsAndRespectsOperationLimit() {
        ItemStackHandler source = new ItemStackHandler(2);
        source.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 32));
        source.setStackInSlot(1, new ItemStack(Items.COBBLESTONE, 32));
        ItemStackHandler destination = new ItemStackHandler(2);
        TransferProgram program = new TransferProgram("iron", "a", "b",
                new TransferFilter(TransferFilter.Mode.WHITELIST, "minecraft:iron_ingot"),
                100, 16, 1, 64, TransferCondition.ALWAYS, TransferSchedule.everyTick());

        int moved = new TransferProgramExecutor().execute(program, source, destination);

        assertEquals(16, moved);
        assertEquals(16, source.getStackInSlot(0).getCount());
        assertEquals(16, destination.getStackInSlot(0).getCount());
        assertEquals(32, source.getStackInSlot(1).getCount());
    }

    @Test
    void fillsLaterDestinationSlotWhenFirstSlotCannotAccept() {
        ItemStackHandler source = new ItemStackHandler(1);
        source.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 16));
        ItemStackHandler destination = new ItemStackHandler(3);
        destination.setStackInSlot(0, new ItemStack(Items.COBBLESTONE, 64));

        TransferProgram program = new TransferProgram("iron", "a", "b",
                new TransferFilter(TransferFilter.Mode.WHITELIST, "minecraft:iron_ingot"),
                100, 16, 1, 64, TransferCondition.ALWAYS, TransferSchedule.everyTick());

        int moved = new TransferProgramExecutor().execute(program, source, destination);

        assertEquals(16, moved);
        assertEquals(16, destination.getStackInSlot(1).getCount());
        assertEquals(0, source.getStackInSlot(0).getCount());
    }
}
