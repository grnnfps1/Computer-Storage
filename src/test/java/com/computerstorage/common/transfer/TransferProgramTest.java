package com.computerstorage.common.transfer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferProgramTest {
    @Test
    void programContainsCompleteRouteConfiguration() {
        TransferProgram p = new TransferProgram(
                "ore-import", "chest-a", "computer", 
                new TransferFilter(TransferFilter.Mode.WHITELIST, "minecraft:iron_ingot"),
                100, 64, 128, 4096, TransferCondition.ALWAYS,
                new TransferSchedule(20, 0));

        assertEquals("ore-import", p.id());
        assertEquals("chest-a", p.sourceId());
        assertEquals("computer", p.destinationId());
        assertEquals(100, p.priority());
        assertEquals(64, p.maxItemsPerOperation());
        assertEquals(128, p.minSourceAmount());
        assertEquals(4096, p.maxDestinationAmount());
    }

    @Test
    void invalidLimitsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new TransferProgram(
                "x", "a", "b", new TransferFilter(TransferFilter.Mode.ALL, ""),
                0, 0, 0, 0, TransferCondition.ALWAYS, TransferSchedule.everyTick()));
    }
}
