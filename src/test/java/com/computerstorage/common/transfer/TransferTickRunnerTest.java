package com.computerstorage.common.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TransferTickRunnerTest {
    @Test
    void scheduledProgramExecutesAgainstResolvedHandlers() {
        ItemStackHandler source = new ItemStackHandler(1);
        source.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 10));
        ItemStackHandler destination = new ItemStackHandler(1);
        TransferProgram program = new TransferProgram("p", "source", "destination",
                new TransferFilter(TransferFilter.Mode.ALL, ""), 10, 4, 1, 64,
                TransferCondition.ALWAYS, new TransferSchedule(20, 5));
        var store = new TransferProgramStore();
        store.add(program);
        var runner = new TransferTickRunner(new TransferProgramRepository(store),
                id -> id.equals("source") ? source : id.equals("destination") ? destination : null);
        var runtimes = new HashMap<String, TransferRuleRuntime>();

        assertFalse(runner.run(program, 4, false, runtimes).attempted());
        TransferResult result = runner.run(program, 5, false, runtimes);
        assertTrue(result.attempted());
        assertEquals(4, result.moved());
        assertEquals(6, source.getStackInSlot(0).getCount());
        assertEquals(4, destination.getStackInSlot(0).getCount());
    }
}
