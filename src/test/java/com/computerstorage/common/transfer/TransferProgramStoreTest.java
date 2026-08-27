package com.computerstorage.common.transfer;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferProgramStoreTest {
    private static TransferProgram program() {
        return new TransferProgram("ore", "source", "storage",
                new TransferFilter(TransferFilter.Mode.WHITELIST, "minecraft:iron_ingot"),
                100, 64, 128, 4096, TransferCondition.REDSTONE_HIGH,
                new TransferSchedule(20, 5));
    }

    @Test
    void duplicateIdsAreRejected() {
        TransferProgramStore store = new TransferProgramStore();
        assertTrue(store.add(program()));
        assertFalse(store.add(program()));
        assertEquals(1, store.programs().size());
    }

    @Test
    void programsSurviveNbtRoundTrip() {
        TransferProgramStore source = new TransferProgramStore();
        source.add(program());
        CompoundTag tag = new CompoundTag();
        source.save(tag);

        TransferProgramStore restored = new TransferProgramStore();
        restored.load(tag);
        assertEquals(1, restored.programs().size());
        TransferProgram p = restored.programs().get(0);
        assertEquals("ore", p.id());
        assertEquals("minecraft:iron_ingot", p.filter().itemId());
        assertEquals(20, p.schedule().intervalTicks());
        assertEquals(5, p.schedule().offsetTicks());
    }
}
